package com.crystal.kip.service.sourcecheck;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** Compares downloaded source bytes with the previous successful fingerprint. */
@Service
public class SourceChecker {

    public enum Status {
        FIRST_CHECK,
        UNCHANGED,
        CHANGED,
    }

    public record Result(String sourceUrl, Status status, String message) {}

    private final Path storageFolder;

    public SourceChecker(@Value("${kip.source-checker.storage-directory:./source-fingerprints}") String storageDirectory) {
        if (storageDirectory == null || storageDirectory.isBlank()) {
            throw new IllegalArgumentException("Fingerprint storage directory is required");
        }
        storageFolder = Path.of(storageDirectory);
    }

    /** Download failures propagate to the caller without replacing the saved fingerprint. */
    public synchronized Result checkSource(String sourceUrl) throws IOException {
        validateUrl(sourceUrl);
        String fingerprint = getFingerprint(sourceUrl);
        if (fingerprint == null || !fingerprint.matches("[a-f0-9]{64}")) {
            throw new IOException("Downloaded fingerprint is invalid");
        }
        String urlId = HexFormat.of().formatHex(sha256().digest(sourceUrl.getBytes(StandardCharsets.UTF_8)));
        Path savedFile = storageFolder.resolve(urlId + ".txt");
        Files.createDirectories(storageFolder);

        if (!Files.exists(savedFile)) {
            Files.writeString(savedFile, fingerprint, StandardCharsets.UTF_8);
            return new Result(sourceUrl, Status.FIRST_CHECK, "First check: fingerprint saved.");
        }

        String previous = Files.readString(savedFile, StandardCharsets.UTF_8).trim();
        if (!previous.matches("[a-f0-9]{64}")) {
            throw new IOException("Saved fingerprint is invalid: " + savedFile);
        }
        if (fingerprint.equals(previous)) {
            return new Result(sourceUrl, Status.UNCHANGED, "Unchanged since the last successful check.");
        }

        Files.writeString(savedFile, fingerprint, StandardCharsets.UTF_8);
        return new Result(sourceUrl, Status.CHANGED, "Source changed since the last successful check. Manual review needed.");
    }

    public String getFingerprint(String sourceUrl) throws IOException {
        URI uri = validateUrl(sourceUrl);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(10_000);
        connection.setRequestProperty("User-Agent", "KIP-SourceChecker/1.0");
        try {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) throw new IOException("Download failed: HTTP " + status);
            MessageDigest digest = sha256();
            try (var input = connection.getInputStream()) {
                byte[] buffer = new byte[8192];
                int count;
                while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
            }
            return HexFormat.of().formatHex(digest.digest());
        } finally {
            connection.disconnect();
        }
    }

    private static URI validateUrl(String sourceUrl) {
        if (sourceUrl == null || sourceUrl.isBlank()) throw new IllegalArgumentException("Source URL is required");
        URI uri = URI.create(sourceUrl);
        if (
            (!"https".equalsIgnoreCase(uri.getScheme()) && !"http".equalsIgnoreCase(uri.getScheme())) ||
            uri.getHost() == null ||
            uri.getUserInfo() != null
        ) {
            throw new IllegalArgumentException("Use an HTTP or HTTPS URL with a host and no embedded credentials");
        }
        return uri;
    }

    private static MessageDigest sha256() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("This Java runtime does not support SHA-256", e);
        }
    }
}
