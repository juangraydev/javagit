package com.javagit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class App {
	public static void main(String[] args) throws InvalidRemoteException, TransportException, GitAPIException, IOException {
		// TODO Auto-generated method stub
		String URL = "https://github.com/juangraydev/client.git";
		String username = "juangraydev";
		String password = "NaNO2801";
		gitSize(URL,username, password);
		
	}
	
	private static void gitSize(String URL, String username, String password) throws IOException, InvalidRemoteException, TransportException, GitAPIException {
		String sourceFile;
		Path folder = Files.createTempDirectory("temp");
		
		try (Git result = Git.cloneRepository()
		  .setURI(URL)
		  .setBranch("refs/heads/master")
		  .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
		  .setDirectory(folder.toFile())
		  .call()){
			String dir = result.getRepository().getDirectory().toString();
			sourceFile = dir.substring(0,dir.indexOf("\\.git")).trim();
		}
	
	
        FileOutputStream fos = new FileOutputStream(sourceFile+".zip");
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
 
        zipFile(fileToZip, fileToZip.getName(), zipOut);
        zipOut.close();
        fos.close();
        
		
        Path path = Paths.get(sourceFile+".zip");
        
        try {
            long bytes = Files.size(path);
            System.out.println(String.format("%,d bytes", bytes));
            System.out.println(String.format("%,d kilobytes", bytes / 1024));
            File tempZip = new File(sourceFile+".zip");
            tempZip.delete();
            deleteFolder(fileToZip); 

        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }
        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {

            	if(childFile.getName() != ".git") {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
            	}
            }
            return;
        }
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }	
	
	private static void deleteFolder(File file){
	      for (File subFile : file.listFiles()) {
	         if(subFile.isDirectory()) {
	            deleteFolder(subFile);
	         } else {
	            subFile.delete();
	         }
	      }
	      file.delete();
	   }
	
}
