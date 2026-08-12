package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.DocumentStatusEnum;
import com.vertex.stockflow.common.enums.IssueTypeEnum;
import com.vertex.stockflow.common.enums.RefTypeEnum;
import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.repository.*;
import com.vertex.stockflow.service.impl.OutboundServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboundVoidRefTypeTest {

    @Mock private OutboundRepository outboundRepository;
    @Mock private OutboundDetailRepository outboundDetailRepository;
    @Mock private WarehouseRepository warehouseRepository;
    @Mock private CustomerRepository customerRepository;
    @Mock private LotRepository lotRepository;
    @Mock private StorageLocationRepository storageLocationRepository;
    @Mock private UserRepository userRepository;
    @Mock private InventoryService inventoryService;
    @Mock private AuditLogService auditLogService;

    @InjectMocks
    private OutboundServiceImpl outboundService;

    @Test
    @DisplayName("voidOutbound → inventory reversal uses OUTBOUND_VOID, not OUTBOUND")
    void voidOutbound_usesOutboundVoidRefType() {
        // Arrange
        UserEntity creator = UserEntity.builder().id(1).email("staff@test.com").role(RoleEnum.STAFF).build();
        UserEntity voider = UserEntity.builder().id(2).email("accountant@test.com").role(RoleEnum.ACCOUNTANT).build();

        WarehouseEntity warehouse = WarehouseEntity.builder().id(10).build();

        OutboundEntity outbound = OutboundEntity.builder()
                .id(100)
                .code("PX-000100")
                .status(DocumentStatusEnum.POSTED)
                .warehouse(warehouse)
                .createdBy(creator)
                .build();

        ProductEntity product = ProductEntity.builder().id(20).build();
        LotEntity lot = LotEntity.builder().id(30).build();
        StorageLocationEntity location = StorageLocationEntity.builder().id(40).build();

        OutboundDetailEntity detail = OutboundDetailEntity.builder()
                .outbound(outbound)
                .product(product)
                .lot(lot)
                .location(location)
                .quantity(5)
                .build();

        when(outboundRepository.findById(100)).thenReturn(Optional.of(outbound));
        when(userRepository.findByEmail("accountant@test.com")).thenReturn(Optional.of(voider));
        when(outboundDetailRepository.findByOutbound_IdIn(List.of(100))).thenReturn(List.of(detail));
        when(outboundRepository.save(any())).thenReturn(outbound);

        User actor = new User("accountant@test.com", "pass",
                List.of(new SimpleGrantedAuthority("ROLE_ACCOUNTANT")));

        // Act
        outboundService.voidOutbound(100, "Sai thông tin", actor);

        // Assert — verify the reversal transaction uses OUTBOUND_VOID
        ArgumentCaptor<RefTypeEnum> refTypeCaptor = ArgumentCaptor.forClass(RefTypeEnum.class);
        verify(inventoryService).updateInventory(
                eq(10), eq(20), eq(30), eq(40),
                eq(5),  // positive = reversal
                refTypeCaptor.capture(),
                eq(100), eq(2));

        assertEquals(RefTypeEnum.OUTBOUND_VOID, refTypeCaptor.getValue(),
                "Bút toán đảo phải dùng OUTBOUND_VOID, không phải OUTBOUND");
    }
}
