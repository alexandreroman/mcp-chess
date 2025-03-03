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

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@RegisterReflection(memberCategories = MemberCategory.INVOKE_DECLARED_METHODS)
class McpConfig {
    private final Logger logger = LoggerFactory.getLogger(McpConfig.class);

    @Bean
    ToolCallbackProvider chessToolsProvider(ChessTools chessTools) {
        // Register Spring AI tools as MCP tools.
        return MethodToolCallbackProvider.builder().toolObjects(chessTools).build();
    }

    @Bean
    public List<McpServerFeatures.SyncPromptRegistration> prompts() {
        return List.of(
                new McpServerFeatures.SyncPromptRegistration(new McpSchema.Prompt(
                        "start-a-new-game",
                        "A prompt to start playing a chess game",
                        Collections.emptyList()), req -> {
                    final var m = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent("Let's play a chess game. Check that each move is legal. Suggest the best move to play."));
                    return new McpSchema.GetPromptResult("A message to start playing a chess game", List.of(m));
                })
        );
    }
}
