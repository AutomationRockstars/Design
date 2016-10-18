package com.automationrockstars.gir.cli;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.google.common.collect.Lists;

import net.schmizz.sshj.SSHClient;

public class SshConnector {


	private static List<SSHClient> clients = Lists.newCopyOnWriteArrayList();
	
	public static void close(){
		for (SSHClient client : clients){
			try {
				client.close();
			} catch (IOException e) {
			
			}
		}
	}

	private static SSHClient openConnection(String host) throws IOException{
		final SSHClient ssh = new SSHClient();
		clients.add(ssh);
		ssh.connect(host);
		return ssh;
	}

	static SSHClient simpleConnect(String host, String username, String password) throws IOException {
		SSHClient ssh = openConnection(host);
		ssh.authPassword(username, password);
		return ssh;
	}
	
	static SSHClient simpleConnect(String host, String username, Path authFile) throws IOException{
		SSHClient ssh = openConnection(host);
		ssh.authPublickey(username, new String[]{authFile.toFile().getAbsolutePath()});
		return ssh;
	}
	
	public static Terminal connect(String host, String username, String password) throws IOException{
		return new Terminal(simpleConnect(host, username, password).startSession());
	}

	public static Terminal connect(String host, String username, Path authFile) throws IOException{
		return new Terminal(simpleConnect(host, username, authFile).startSession());
	}

}
