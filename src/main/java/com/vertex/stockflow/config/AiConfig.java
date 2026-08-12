package com.vertex.stockflow.config;

import com.vertex.stockflow.service.WarehouseAiTools;
import com.vertex.stockflow.service.WarehouseAssistant;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final String SYSTEM_MESSAGE = """
            Bạn là AI thông minh quản lý kho hàng tên là StockFlow AI.
            Khách hàng đang hỏi bạn về hệ thống quản lý kho hàng.
            Hãy BẮT BUỘC sử dụng các Tools được cung cấp để tra cứu cơ sở dữ liệu thực tế và trả lời khách hàng.
            Trả lời chuyên nghiệp, bằng tiếng Việt.
            Khi người dùng hỏi về số liệu, hãy luôn gọi Tool để lấy dữ liệu mới nhất, không bao giờ đoán số liệu.

            QUY TẮC BẮT BUỘC VỀ HIỂN THỊ DỮ LIỆU:
            - Khi Tool trả về danh sách dữ liệu, bạn PHẢI hiển thị TỪNG MỤC trong danh sách đó cho người dùng.
            - KHÔNG BAO GIỜ được bỏ qua, tóm tắt, hay rút gọn danh sách.
            - Hãy trình bày dữ liệu dưới dạng bảng hoặc danh sách có đánh số rõ ràng.
            - Nếu danh sách có 50 sản phẩm, bạn PHẢI liệt kê đủ 50 sản phẩm.
            """;

    @Bean
    WarehouseAssistant warehouseAssistant(ChatLanguageModel chatLanguageModel,
                                          WarehouseAiTools warehouseAiTools) {
        return AiServices.builder(WarehouseAssistant.class)
                .chatLanguageModel(chatLanguageModel)
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.withMaxMessages(20))
                .tools(warehouseAiTools)
                .systemMessageProvider(memoryId -> SYSTEM_MESSAGE)
                .build();
    }
}
