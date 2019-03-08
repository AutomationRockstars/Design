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

import com.google.common.collect.Lists;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;

import java.io.IOException;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.List;

public class SshConnector {


    private static List<SSHClient> clients = Lists.newCopyOnWriteArrayList();

    public static void close() {
        for (SSHClient client : clients) {
            try {
                client.close();
            } catch (IOException e) {

            }
        }
    }

    private static SSHClient openConnection(String host) throws IOException {
        final SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(
                new HostKeyVerifier() {
                    @Override
                    public boolean verify(String s, int i, PublicKey publicKey) {
                        return true;
                    }
                });
        clients.add(ssh);
        ssh.connect(host);
        return ssh;
    }

    static SSHClient simpleConnect(String host, String username, String password) throws IOException {
        SSHClient ssh = openConnection(host);
        ssh.authPassword(username, password);
        return ssh;
    }

    static SSHClient simpleConnect(String host, String username, Path authFile) throws IOException {
        SSHClient ssh = openConnection(host);
        ssh.authPublickey(username, new String[]{authFile.toFile().getAbsolutePath()});
        return ssh;
    }

    public static Terminal connect(String host, String username, String password) throws IOException {
        return new Terminal(simpleConnect(host, username, password).startSession());
    }

    public static Terminal connect(String host, String username, Path authFile) throws IOException {
        return new Terminal(simpleConnect(host, username, authFile).startSession());
    }

}
