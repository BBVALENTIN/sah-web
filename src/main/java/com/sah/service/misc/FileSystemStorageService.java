package com.sah.service.misc;

import com.sah.FileUpload.StorageException;
import com.sah.FileUpload.StorageFileNotFoundException;
import com.sah.FileUpload.StorageProperties;
import com.sah.FileUpload.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootLocation;
    private static final long MAX_UPLOAD_BYTES = 5L * 1024 * 1024;
    private static final int AVATAR_SIZE = 256;

    @Autowired
    public FileSystemStorageService(StorageProperties storageProperties) {
        if(storageProperties.getLocation().trim().length() == 0) {
            throw new StorageException("File upload location cannot be empty");
        }

        this.rootLocation = Paths.get(storageProperties.getLocation());
    }

    @Override
    public String store(MultipartFile file) {
        try {
            if(file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
            }

            String filename = java.util.UUID.randomUUID().toString();

            Path destinationFile = this.rootLocation.resolve(
                    Paths.get(filename))
                    .normalize().toAbsolutePath();

            if(!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside of current directory");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch(IOException e) {
            throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
        }
    }

    @Override
    public String storeImage(MultipartFile file) throws IOException {

        if(file.isEmpty())
            throw new StorageException("Failed to store empty file " + file.getOriginalFilename());

        if(file.getSize() > MAX_UPLOAD_BYTES)
            throw new StorageException("File too large");

        byte[] resizedWebp;
        try {
            resizedWebp = ImageResizer.resizeToAvatar(file.getBytes(), AVATAR_SIZE);
        } catch (IOException e) {
            throw new StorageException("Uploaded file is not a valid image", e);
        }

        String filename = java.util.UUID.randomUUID().toString() + ".jpeg";
        Path destionationFile = this.rootLocation.resolve(Paths.get(filename))
                .normalize().toAbsolutePath();

        if(!destionationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
            throw new StorageException("Cannot store file outside of current dir");
        }

        Files.write(destionationFile, resizedWebp,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING);

        return filename;
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }
}
