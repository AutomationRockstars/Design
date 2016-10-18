package com.automationrockstars.gir.cli;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Throwables;

import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;

public class Terminal implements Closeable{

	private final Session session;
	 Shell shell;
	Terminal(Session session){
		this.session = session;
		try {
			session.allocateDefaultPTY();
			shell = session.startShell();
			new StreamCopier(shell.getInputStream(), output, net.schmizz.sshj.common.LoggerFactory.DEFAULT )
            .bufSize(shell.getLocalMaxPacketSize())
            .spawn("stdout");
			 new StreamCopier(shell.getErrorStream(), error,net.schmizz.sshj.common.LoggerFactory.DEFAULT)
             .bufSize(shell.getLocalMaxPacketSize())
             .spawn("stderr");
			
		} catch ( IOException e) {
			Throwables.propagate(e);
		}
		
	}
	
	
	final ByteArrayOutputStream output = new ByteArrayOutputStream();
	final ByteArrayOutputStream error = new ByteArrayOutputStream();
	
	void write(String text){
		try {
			shell.getOutputStream().write(text.getBytes());
		} catch (IOException e) {
			Throwables.propagate(e);
		}
	}
	
	ExecResult exec(String command,long timeout) throws IOException{
		final Command cmd = session.exec(command);
			String result = IOUtils.readFully(cmd.getInputStream()).toString();
			String error = IOUtils.readFully(cmd.getErrorStream()).toString();
			cmd.join(timeout,TimeUnit.MILLISECONDS);
			int exit = cmd.getExitStatus(); 
			return new ExecResult(result,error,exit);
	}

	String getStdOut(){
		return output.toString();
	}
	
	String getStdErr(){
		return error.toString();
	}
	@Override
	public void close() throws IOException {
		session.close();
	}
	
	
}
