/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */

package com.sun.enterprise.admin.servermgmt.services;

import com.sun.enterprise.universal.io.SmartFile;
import com.sun.enterprise.universal.process.ProcessManager;
import com.sun.enterprise.universal.process.ProcessManagerException;
import com.sun.enterprise.util.OS;
import com.sun.enterprise.util.ObjectAnalyzer;
import com.sun.enterprise.util.StringUtils;
import com.sun.enterprise.util.SystemPropertyConstants;
import com.sun.enterprise.util.io.FileUtils;
import com.sun.enterprise.util.io.ServerDirs;
import java.io.*;
import java.util.*;
import java.util.Map;
import static com.sun.enterprise.admin.servermgmt.services.Constants.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Warning: there is lots of file twiddling going on in this class.  It is the nature
 * of the beast.
 * @author Byron Nevins
 */
public class WindowsService extends NonSMFServiceAdapter {

    static boolean apropos() {
        return OS.isWindowsForSure();
    }

    WindowsService(ServerDirs dirs, AppserverServiceType type) {
        super(dirs, type);
        if (!apropos()) {
            // programmer error
            throw new IllegalArgumentException(Strings.get("internal.error",
                    "Constructor called but Windows Services are not available."));
        }
    }


    // bnevins, Aug 2010.  The unfortunate FAT interface of the Service interface makes
    // it confusing -- this method is really the only one that does something --
    // all the other methods do configuration.
    @Override
    public final void createServiceInternal() throws RuntimeException {
        try {
            trace("**********   Object Dump  **********\n" + this.toString());

            if (uninstall() == 0 && !info.dryRun)
                System.out.println(Strings.get("windows.services.uninstall.good"));
            else
                trace("No preexisting Service with that id and/or name was found");

            install();
        }
        catch (ProcessManagerException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public final String getSuccessMessage() {
        if (info.dryRun)
            return Strings.get("dryrun");

        return Strings.get("WindowsServiceCreated", info.serviceName,
                getServerDirs().getServerName() + " GlassFish Server", 
                getServerDirs().getServerDir(), targetXml, targetWin32Exe);
    }

    @Override
    public final void writeReadmeFile(String msg) {
        // TODO 1/19/2010 bnevins duplicated in SMFService
        File f = new File(getServerDirs().getServerDir(), README);

        if(StringUtils.ok(xmlFileCopy))
            msg += xmlFileCopy;

        ServicesUtils.appendTextToFile(f, msg);
    }

    @Override
    public String toString() {
        return ObjectAnalyzer.toString(this);
    }

    @Override
    public final String getLocationArgsStart() {
        if (isDomain()) {
            return makeStartArg("--domaindir")
                    + makeStartArg(getServerDirs().getServerParentDir().getPath());
        }
        else {
            return makeStartArg("--nodedir")
                    + makeStartArg(getServerDirs().getServerGrandParentDir().getPath().replace('\\', '/'))
                    + makeStartArg("--node")
                    + makeStartArg(getServerDirs().getServerParentDir().getName());
        }
    }

    @Override
    public final String getLocationArgsStop() {
        if (isDomain()) {
            return makeStopArg("--domaindir")
                    + makeStopArg(getServerDirs().getServerParentDir().getPath());
        }
        else {
            return makeStopArg("--nodedir")
                    + makeStopArg(getServerDirs().getServerGrandParentDir().getPath().replace('\\', '/'))
                    + makeStopArg("--node")
                    + makeStopArg(getServerDirs().getServerParentDir().getName());
        }
    }

    @Override
    public final void initializeInternal() {
        try {
            setTemplateFile(TEMPLATE_FILE_NAME);
            setSourceWin32Exe();
            targetDir = new File(getServerDirs().getServerDir(), TARGET_DIR);
            targetDir.mkdirs(); // just in case...
            if (!targetDir.isDirectory())
                throw new RuntimeException(Strings.get("noTargetDir", targetDir));
            targetWin32Exe = new File(targetDir, getServerDirs().getServerName() + "Service.exe");
            targetXml = new File(targetDir, getServerDirs().getServerName() + "Service.xml");
            handlePreExisting(targetWin32Exe, targetXml, info.force);
            FileUtils.copy(sourceWin32Exe, targetWin32Exe);
            trace("Copied from " + sourceWin32Exe + " to " + targetWin32Exe);
            getTokenMap().put(CREDENTIALS_START_TN, getAsadminCredentials("startargument"));
            getTokenMap().put(CREDENTIALS_STOP_TN, getAsadminCredentials("stopargument"));
            ServicesUtils.tokenReplaceTemplateAtDestination(getTokenMap(), getTemplateFile().getPath(), targetXml.getPath());
            trace("Target XML file written: " + targetXml);
        }
        catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    ///////////////////////////////////////////////////////////////////////
    //////////////////////////   ALL PRIVATE BELOW    /////////////////////
    ///////////////////////////////////////////////////////////////////////
    private void setSourceWin32Exe() throws IOException {
        sourceWin32Exe = new File(info.libDir, SOURCE_WIN32_EXE_FILENAME);

        if (!sourceWin32Exe.isFile()) {
            // copy it from inside this jar to the file system
            InputStream in = getClass().getResourceAsStream("/lib/" + SOURCE_WIN32_EXE_FILENAME);
            FileOutputStream out = new FileOutputStream(sourceWin32Exe);
            copyStream(in, out);
            trace("Copied from inside the jar to " + sourceWin32Exe);
        }
        trace("Source executable: " + sourceWin32Exe);
    }

    private static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[16384];
        int len;
        while ((len = in.read(buf)) >= 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }



    /**
     * If we had a crude "Template Language" we could do some if/else stuff
     * right in the template.  We don't have that and it is not worth the development
     * cost to add it.  So what we do is just drop %%%CREDENTIALS%%% into the xml
     * template at the right place.  We replace with one space character for default
     * credentials.  If there ARE credentials we replace with XML elements
     *
     * @return the hunk of XML
     */
    private String getAsadminCredentials(String elem) {
        // 1 -- no auth of any kind needed -- by definition when there is no
        // password file
        // note: you do NOT want to give a "--user" arg -- it can only appear
        // if there is a password file too
        if (info.passwordFile == null)
            return " ";

        // 2. --
        String user = info.appserverUser; // might be null

        String begin = "<" + elem + ">";
        String end = "</" + elem + ">\n";
        StringBuilder sb = new StringBuilder();

        if (user != null) {
            StringBuilder append = sb.append(" " + begin + "--user" + end);
            sb.append("  ").append(begin).append(user).append(end);
        }
        sb.append("  ").append(begin).append("--passwordfile").append(end);
        sb.append("  ").append(begin).append(info.passwordFile.getPath()).append(end);
        sb.append("  "); // such obsessive attention to detail!!! :-)

        return sb.toString();
    }

    private int uninstall() throws ProcessManagerException {
        if (info.dryRun || !targetWin32Exe.canExecute())
            return 0;
        // it is NOT an error to not be able to uninstall
        ProcessManager mgr = new ProcessManager(targetWin32Exe.getPath(), "uninstall");
        mgr.execute();
        trace("Uninstall STDERR: " + mgr.getStderr());
        trace("Uninstall STDOUT: " + mgr.getStdout());
        return mgr.getExitValue();
    }

    private void install() throws ProcessManagerException {
        // it IS an error to not be able to install

        if (info.dryRun) {
            try {
                // dry-run not so useful on Windows.  Very useful on UNIX...
                xmlFileCopy = Strings.get("xmlfiledump") + FileUtils.readSmallFile(targetXml);
            }
            catch (IOException ex) {
                // oh well....
            }

            targetXml.delete();
            targetWin32Exe.delete();
        }
        else {
            ProcessManager mgr = new ProcessManager(targetWin32Exe.getPath(), "install");
            mgr.execute();
            int ret = mgr.getExitValue();

            if (ret != 0)
                throw new RuntimeException(Strings.get("windows.services.install.bad",
                        "" + ret, mgr.getStdout(), mgr.getStderr()));

            trace("Install STDERR: " + mgr.getStderr());
            trace("Install STDOUT: " + mgr.getStdout());
        }
    }

    private void handlePreExisting(File targetWin32Exe, File targetXml, boolean force) {
        if (targetWin32Exe.exists() || targetXml.exists()) {
            if (force) {
                targetWin32Exe.delete();
                targetXml.delete();
                // we call this same method to make sur they were deleted
                handlePreExisting(targetWin32Exe, targetXml, false);
            }
            else {
                throw new RuntimeException(Strings.get("services.alreadyCreated", 
                        new File(targetDir, getServerDirs().getServerName() + "Service").toString() + ".*",
                        "del"));
            }
        }
    }

    private String makeStartArg(String s) {
        return "  " + START_ARG_START + s + START_ARG_END + "\n";
    }

    private String makeStopArg(String s) {
        return "  " + STOP_ARG_START + s + STOP_ARG_END + "\n";
    }


    private static boolean ok(String s) {
        return s != null && s.length() > 0;
    }

    private static final String SOURCE_WIN32_EXE_FILENAME = "winsw.exe";
    private static final String TARGET_DIR = "bin";
    private static final String TEMPLATE_FILE_NAME = "Domain-service-winsw.xml.template";
    private File sourceWin32Exe;
    private File targetDir;
    private File targetXml;
    private File targetWin32Exe;
    private String xmlFileCopy;
}
