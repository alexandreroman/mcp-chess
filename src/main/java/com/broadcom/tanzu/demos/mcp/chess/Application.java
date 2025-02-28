/*
 * Copyright (c) 2025 Broadcom, Inc. or its affiliates
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.broadcom.tanzu.demos.mcp.chess;

import com.github.alexandreroman.chessimage.ChessRenderer;
import org.graalvm.nativeimage.ImageInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws IOException, URISyntaxException {
        // Run this app in headless move (no AWT window required).
        System.setProperty("java.awt.headless", "true");

        // Configure native library path on Windows when running in GraalVM.
        if (isRunningInGraalVM()) {
            final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            if (isWindows) {
                final var cur = new File(Application.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
                // Load native libraries from the same directory of the current process.
                System.setProperty("java.library.path", cur.getAbsolutePath());
            }
        }

        // Enable a "test" mode.
        if (args.length > 0 && "test".equals(args[0])) {
            final var f = new File("mcp-chess-test.png");
            try (final var out = new FileOutputStream(f)) {
                new ChessRenderer().render("r1bk3r/p2pBpNp/n4n2/1p1NP2P/6P1/3P4/P1P1K3/q5b1", out);
            }
        }

        SpringApplication.run(Application.class, args);
    }

    private static boolean isRunningInGraalVM() {
        return ImageInfo.inImageRuntimeCode() && ImageInfo.isExecutable();
    }
}
