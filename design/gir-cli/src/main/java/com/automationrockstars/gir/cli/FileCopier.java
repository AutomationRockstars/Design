/*
 * <!--
 *     Copyright (c) 2015-2019 Automation RockStars Ltd.
 *     All rights reserved. This program and the accompanying materials
 *     are made available under the terms of the Apache License v2.0
 *     which accompanies this distribution, and is available at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Contributors:
 *         Automation RockStars
 *  -->
 */
package com.automationrockstars.gir.cli;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import net.schmizz.sshj.xfer.scp.SCPFileTransfer;

import java.io.IOException;
import java.nio.file.Path;

public class FileCopier {

    private SCPFileTransfer scp;

    public FileCopier(String hostname, String user, String password) throws IOException {
        SSHClient client = SshConnector.simpleConnect(hostname, user, password);
        client.useCompression();
        scp = client.newSCPFileTransfer();

    }

    public FileCopier(String hostname, String user, Path passFile) throws IOException {
        SSHClient client = SshConnector.simpleConnect(hostname, user, passFile);
        client.useCompression();
        scp = client.newSCPFileTransfer();
    }

    public void put(String src, String dest) throws IOException {
        scp.upload(new FileSystemFile(src), dest);
    }

    public void get(String src, String dest) throws Exception {
        scp.download(src, new FileSystemFile(dest));
    }
}
