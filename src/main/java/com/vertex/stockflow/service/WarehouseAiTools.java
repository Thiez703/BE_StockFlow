package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.StatusEnum;
import com.vertex.stockflow.common.enums.RoleEnum;
import com.vertex.stockflow.entity.*;
import com.vertex.stockflow.repository.*;
import dev.langchain4j.agent.tool.Tool;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WarehouseAiTools {

    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryRepository inventoryRepository;
    private final LotRepository lotRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final StorageLocationRepository storageLocationRepository;
    private final InboundRepository inboundRepository;
    private final OutboundRepository outboundRepository;
    private final StocktakeRepository stocktakeRepository;
    private final TransferRepository transferRepository;
    private final ReportRepository reportRepository;
    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final UserRepository userRepository;

    // ==================== ĐẾM SỐ LƯỢNG ====================

    @Tool("Đếm tổng số sản phẩm hiện có trong hệ thống kho hàng")
    public long countProducts() {
        return productRepository.count();
    }

    @Tool("Đếm tổng số nhà kho hiện có trong hệ thống")
    public long countWarehouses() {
        return warehouseRepository.count();
    }

    @Tool("Đếm tổng số danh mục sản phẩm hiện có trong hệ thống")
    public long countCategories() {
        return categoryRepository.count();
    }

    @Tool("Đếm tổng số nhà cung cấp (supplier) hiện có trong hệ thống")
    public long countSuppliers() {
        return supplierRepository.count();
    }

    @Tool("Đếm tổng số khách hàng hiện có trong hệ thống")
    public long countCustomers() {
        return customerRepository.count();
    }

    @Tool("Đếm tổng số lô hàng (lot) hiện có trong hệ thống")
    public long countLots() {
        return lotRepository.count();
    }

    @Tool("Đếm tổng số phiếu nhập kho (inbound) hiện có trong hệ thống")
    public long countInbounds() {
        return inboundRepository.count();
    }

    @Tool("Đếm tổng số phiếu xuất kho (outbound) hiện có trong hệ thống")
    public long countOutbounds() {
        return outboundRepository.count();
    }

    @Tool("Đếm tổng số phiếu kiểm kê (stocktake) hiện có trong hệ thống")
    public long countStocktakes() {
        return stocktakeRepository.count();
    }

    @Tool("Đếm tổng số phiếu điều chuyển vị trí hiện có trong hệ thống")
    public long countTransfers() {
        return transferRepository.count();
    }

    // ==================== DANH SÁCH SẢN PHẨM ====================

    @Tool("Lấy danh sách tất cả sản phẩm gồm mã sản phẩm, tên, đơn vị tính, tồn kho tối thiểu và trạng thái")
    public String listAllProducts() {
        List<ProductEntity> products = productRepository.findAll();
        if (products.isEmpty()) return "Chưa có sản phẩm nào trong hệ thống.";
        String data = products.stream()
                .map(p -> String.format("- Mã: %s | Tên: %s | ĐVT: %s | Tồn tối thiểu: %s | Trạng thái: %s",
                        p.getCode(), p.getName(), p.getUnit(),
                        p.getMinStock() != null ? p.getMinStock() : "N/A",
                        p.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + products.size() + " sản phẩm:\n" + data;
    }

    @Tool("Kiểm tra sản phẩm có tồn tại theo mã sản phẩm (product code) hay không")
    public String checkProductByCode(String code) {
        boolean exists = productRepository.existsByCode(code);
        if (exists) {
            return "Sản phẩm với mã '" + code + "' TỒN TẠI trong hệ thống.";
        }
        return "KHÔNG tìm thấy sản phẩm nào với mã '" + code + "'.";
    }

    // ==================== DANH SÁCH NHÀ KHO ====================

    @Tool("Lấy danh sách tất cả nhà kho gồm mã kho, tên kho và địa chỉ")
    public String listAllWarehouses() {
        List<WarehouseEntity> warehouses = warehouseRepository.findAll();
        if (warehouses.isEmpty()) return "Chưa có nhà kho nào trong hệ thống.";
        String data = warehouses.stream()
                .map(w -> String.format("- Mã: %s | Tên: %s | Địa chỉ: %s",
                        w.getCode(), w.getName(),
                        w.getAddress() != null ? w.getAddress() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + warehouses.size() + " nhà kho:\n" + data;
    }

    @Tool("Kiểm tra nhà kho có tồn tại theo mã kho (warehouse code) hay không")
    public String checkWarehouseByCode(String code) {
        boolean exists = warehouseRepository.existsByCode(code);
        if (exists) {
            return "Nhà kho với mã '" + code + "' TỒN TẠI trong hệ thống.";
        }
        return "KHÔNG tìm thấy nhà kho nào với mã '" + code + "'.";
    }

    // ==================== DANH MỤC ====================

    @Tool("Lấy danh sách danh mục gốc (root categories) không có danh mục cha")
    public String listRootCategories() {
        List<CategoryEntity> roots = categoryRepository.findByParentIsNull();
        if (roots.isEmpty()) return "Chưa có danh mục nào trong hệ thống.";
        String data = roots.stream()
                .map(c -> String.format("- ID: %d | Tên: %s | Trạng thái: %s",
                        c.getId(), c.getName(), c.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + roots.size() + " danh mục gốc:\n" + data;
    }

    @Tool("Lấy danh sách danh mục con theo ID danh mục cha")
    public String listChildCategories(int parentId) {
        List<CategoryEntity> children = categoryRepository.findByParentId(parentId);
        if (children.isEmpty()) return "Danh mục ID=" + parentId + " không có danh mục con nào.";
        String data = children.stream()
                .map(c -> String.format("- ID: %d | Tên: %s | Trạng thái: %s",
                        c.getId(), c.getName(), c.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + children.size() + " danh mục con của danh mục ID=" + parentId + ":\n" + data;
    }

    // ==================== NHÀ CUNG CẤP ====================

    @Tool("Lấy danh sách tất cả nhà cung cấp gồm mã, tên, người liên hệ, SĐT, email, địa chỉ")
    public String listAllSuppliers() {
        List<SupplierEntity> suppliers = supplierRepository.findAll();
        if (suppliers.isEmpty()) return "Chưa có nhà cung cấp nào trong hệ thống.";
        String data = suppliers.stream()
                .map(s -> String.format("- Mã: %s | Tên: %s | Liên hệ: %s | SĐT: %s | Email: %s | Địa chỉ: %s | Trạng thái: %s",
                        s.getCode(), s.getName(),
                        s.getContactPerson() != null ? s.getContactPerson() : "N/A",
                        s.getPhone() != null ? s.getPhone() : "N/A",
                        s.getEmail() != null ? s.getEmail() : "N/A",
                        s.getAddress() != null ? s.getAddress() : "N/A",
                        s.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + suppliers.size() + " nhà cung cấp:\n" + data;
    }

    // ==================== KHÁCH HÀNG ====================

    @Tool("Lấy danh sách tất cả khách hàng gồm tên, SĐT, địa chỉ")
    public String listAllCustomers() {
        List<CustomerEntity> customers = customerRepository.findAll();
        if (customers.isEmpty()) return "Chưa có khách hàng nào trong hệ thống.";
        String data = customers.stream()
                .map(c -> String.format("- ID: %d | Tên: %s | SĐT: %s | Địa chỉ: %s | Trạng thái: %s",
                        c.getId(), c.getName(),
                        c.getPhone() != null ? c.getPhone() : "N/A",
                        c.getAddress() != null ? c.getAddress() : "N/A",
                        c.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + customers.size() + " khách hàng:\n" + data;
    }

    // ==================== TỒN KHO ====================

    @Tool("Lấy thông tin tồn kho tổng hợp theo từng sản phẩm, trả về danh sách sản phẩm kèm tổng số lượng tồn kho")
    public String getStockSummaryByProduct() {
        List<Object[]> stockData = inventoryRepository.sumStockByProduct();
        if (stockData.isEmpty()) return "Chưa có dữ liệu tồn kho.";
        String data = stockData.stream()
                .map(row -> {
                    Integer productId = (Integer) row[0];
                    Number totalQty = (Number) row[1];
                    ProductEntity product = productRepository.findById(productId).orElse(null);
                    String productName = product != null ? product.getName() : "ID=" + productId;
                    return String.format("- %s (Mã: %s): Tồn kho = %s",
                            productName,
                            product != null ? product.getCode() : "N/A",
                            totalQty);
                })
                .collect(Collectors.joining("\n"));
        return "Tồn kho theo sản phẩm:\n" + data;
    }

    @Tool("Lấy thông tin tồn kho tổng hợp theo từng lô hàng (lot), trả về danh sách lô kèm số lượng tồn")
    public String getStockSummaryByLot() {
        List<Object[]> stockData = inventoryRepository.sumStockByLot();
        if (stockData.isEmpty()) return "Chưa có dữ liệu tồn kho theo lô.";
        String data = stockData.stream()
                .map(row -> {
                    Integer lotId = (Integer) row[0];
                    Number totalQty = (Number) row[1];
                    LotEntity lot = lotRepository.findById(lotId).orElse(null);
                    String lotCode = lot != null ? lot.getLotCode() : "ID=" + lotId;
                    String productName = (lot != null && lot.getProduct() != null) ? lot.getProduct().getName() : "N/A";
                    return String.format("- Lô: %s | Sản phẩm: %s | Tồn kho = %s",
                            lotCode, productName, totalQty);
                })
                .collect(Collectors.joining("\n"));
        return "Tồn kho theo lô hàng:\n" + data;
    }

    // ==================== LÔ HÀNG ====================

    @Tool("Lấy danh sách tất cả lô hàng (lot) theo ID sản phẩm, gồm mã lô, ngày sản xuất, hạn sử dụng, trạng thái")
    public String listLotsByProductId(int productId) {
        List<LotEntity> lots = lotRepository.findByProductId(productId);
        if (lots.isEmpty()) return "Sản phẩm ID=" + productId + " chưa có lô hàng nào.";
        ProductEntity product = productRepository.findById(productId).orElse(null);
        String productName = product != null ? product.getName() : "ID=" + productId;
        String data = lots.stream()
                .map(l -> String.format("- Mã lô: %s | NSX: %s | HSD: %s | Trạng thái: %s",
                        l.getLotCode(),
                        l.getMfgDate() != null ? l.getMfgDate() : "N/A",
                        l.getExpDate(),
                        l.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + lots.size() + " lô hàng của sản phẩm '" + productName + "':\n" + data;
    }

    @Tool("Tìm các lô hàng (lot) sắp hết hạn trong vòng số ngày được chỉ định")
    public String findLotsExpiringSoon(int withinDays) {
        LocalDate deadline = LocalDate.now().plusDays(withinDays);
        List<LotEntity> lots = lotRepository.findByStatusAndExpDateBefore(StatusEnum.ACTIVE, deadline);
        if (lots.isEmpty()) return "Không có lô hàng nào sắp hết hạn trong " + withinDays + " ngày tới.";
        String data = lots.stream()
                .map(l -> String.format("- Lô: %s | Sản phẩm: %s | HSD: %s",
                        l.getLotCode(),
                        l.getProduct().getName(),
                        l.getExpDate()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + lots.size() + " lô sắp hết hạn trong " + withinDays + " ngày:\n" + data;
    }

    @Tool("Lấy danh sách lô hàng còn tồn kho (có inventory > 0) của một sản phẩm, sắp xếp theo hạn sử dụng tăng dần (FEFO)")
    public String listAvailableLotsByProduct(int productId) {
        List<LotEntity> lots = lotRepository.findAvailableLotsByProductOrderByExpDateAsc(productId);
        if (lots.isEmpty()) return "Sản phẩm ID=" + productId + " không có lô nào còn tồn kho.";
        ProductEntity product = productRepository.findById(productId).orElse(null);
        String productName = product != null ? product.getName() : "ID=" + productId;
        String data = lots.stream()
                .map(l -> String.format("- Mã lô: %s | HSD: %s | Trạng thái: %s",
                        l.getLotCode(), l.getExpDate(), l.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Lô còn tồn kho của '" + productName + "' (theo thứ tự FEFO):\n" + data;
    }

    // ==================== VỊ TRÍ KHO ====================

    @Tool("Lấy danh sách vị trí lưu kho (storage location) theo ID nhà kho, gồm mã vị trí, hàng, cột, trạng thái")
    public String listStorageLocationsByWarehouseId(int warehouseId) {
        List<StorageLocationEntity> locations = storageLocationRepository.findByWarehouseId(warehouseId);
        if (locations.isEmpty()) return "Nhà kho ID=" + warehouseId + " chưa có vị trí lưu kho nào.";
        WarehouseEntity warehouse = warehouseRepository.findById(warehouseId).orElse(null);
        String whName = warehouse != null ? warehouse.getName() : "ID=" + warehouseId;
        String data = locations.stream()
                .map(sl -> String.format("- Mã: %s | Hàng: %s | Cột: %d | Trạng thái: %s",
                        sl.getLocationCode(), sl.getRowLabel(), sl.getColIndex(), sl.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tổng cộng " + locations.size() + " vị trí trong kho '" + whName + "':\n" + data;
    }

    @Tool("Lấy danh sách vị trí kho vừa mới được cập nhật (thay đổi) gần đây nhất trên toàn hệ thống")
    public String listRecentlyUpdatedLocations() {
        Page<StorageLocationEntity> page = storageLocationRepository.findAll(
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt")));
        List<StorageLocationEntity> locations = page.getContent();
        if (locations.isEmpty()) return "Chưa có vị trí kho nào.";
        String data = locations.stream()
                .map(sl -> String.format("- Mã vị trí: %s | Kho: %s | Cập nhật lúc: %s",
                        sl.getLocationCode(),
                        sl.getWarehouse() != null ? sl.getWarehouse().getName() : "N/A",
                        sl.getUpdatedAt() != null ? sl.getUpdatedAt() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Top 10 vị trí kho vừa được thay đổi gần nhất:\n" + data;
    }


    // ==================== PHIẾU NHẬP KHO ====================

    @Tool("Lấy danh sách phiếu nhập kho (inbound) gần nhất, tối đa 20 phiếu, gồm mã phiếu, nhà cung cấp, kho, trạng thái, ngày tạo")
    public String listRecentInbounds() {
        Page<InboundEntity> page = inboundRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<InboundEntity> inbounds = page.getContent();
        if (inbounds.isEmpty()) return "Chưa có phiếu nhập kho nào.";
        String data = inbounds.stream()
                .map(ib -> String.format("- Mã: %s | NCC: %s | Kho: %s | Trạng thái: %s | Ngày tạo: %s",
                        ib.getCode(),
                        ib.getSupplier().getName(),
                        ib.getWarehouse().getName(),
                        ib.getStatus(),
                        ib.getCreatedAt() != null ? ib.getCreatedAt().toLocalDate() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Tổng " + page.getTotalElements() + " phiếu nhập kho (hiển thị 20 gần nhất):\n" + data;
    }

    // ==================== PHIẾU XUẤT KHO ====================

    @Tool("Lấy danh sách phiếu xuất kho (outbound) gần nhất, tối đa 20 phiếu, gồm mã phiếu, loại xuất, khách hàng, kho, trạng thái, ngày tạo")
    public String listRecentOutbounds() {
        Page<OutboundEntity> page = outboundRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<OutboundEntity> outbounds = page.getContent();
        if (outbounds.isEmpty()) return "Chưa có phiếu xuất kho nào.";
        String data = outbounds.stream()
                .map(ob -> String.format("- Mã: %s | Loại: %s | KH: %s | Kho: %s | Trạng thái: %s | Ngày tạo: %s",
                        ob.getCode(),
                        ob.getIssueType(),
                        ob.getCustomer() != null ? ob.getCustomer().getName() : "N/A",
                        ob.getWarehouse().getName(),
                        ob.getStatus(),
                        ob.getCreatedAt() != null ? ob.getCreatedAt().toLocalDate() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Tổng " + page.getTotalElements() + " phiếu xuất kho (hiển thị 20 gần nhất):\n" + data;
    }

    // ==================== PHIẾU KIỂM KÊ ====================

    @Tool("Lấy danh sách phiếu kiểm kê (stocktake) gần nhất, tối đa 20 phiếu")
    public String listRecentStocktakes() {
        Page<StocktakeEntity> page = stocktakeRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<StocktakeEntity> list = page.getContent();
        if (list.isEmpty()) return "Chưa có phiếu kiểm kê nào.";
        String data = list.stream()
                .map(s -> String.format("- Mã: %s | Kho: %s | Trạng thái: %s | Ngày tạo: %s",
                        s.getCode(),
                        s.getWarehouse().getName(),
                        s.getStatus(),
                        s.getCreatedAt() != null ? s.getCreatedAt().toLocalDate() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Tổng " + page.getTotalElements() + " phiếu kiểm kê (hiển thị 20 gần nhất):\n" + data;
    }

    // ==================== PHIẾU ĐIỀU CHUYỂN ====================

    @Tool("Lấy danh sách phiếu điều chuyển (transfer) gần nhất, tối đa 20 phiếu")
    public String listRecentTransfers() {
        Page<TransferEntity> page = transferRepository.findAll(
                PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<TransferEntity> list = page.getContent();
        if (list.isEmpty()) return "Chưa có phiếu điều chuyển nào.";
        String data = list.stream()
                .map(t -> String.format("- Mã: %s | Kho: %s | Trạng thái: %s | Ngày tạo: %s",
                        t.getCode(),
                        t.getWarehouse().getName(),
                        t.getStatus(),
                        t.getCreatedAt() != null ? t.getCreatedAt().toLocalDate() : "N/A"))
                .collect(Collectors.joining("\n"));
        return "Tổng " + page.getTotalElements() + " phiếu điều chuyển (hiển thị 20 gần nhất):\n" + data;
    }

    // ==================== BÁO CÁO ====================

    @Tool("Lấy báo cáo nhập-xuất-tồn theo kỳ. Tham số: fromDate (ngày bắt đầu dạng yyyy-MM-dd), toDate (ngày kết thúc dạng yyyy-MM-dd). Trả về tồn đầu kỳ, nhập trong kỳ, xuất trong kỳ, tồn cuối kỳ theo từng sản phẩm/lô")
    public String getInventorySummaryReport(String fromDate, String toDate) {
        LocalDateTime from = LocalDate.parse(fromDate).atStartOfDay();
        LocalDateTime to = LocalDate.parse(toDate).atTime(23, 59, 59);
        List<Object[]> data = reportRepository.findInventorySummary(from, to, null);
        if (data.isEmpty()) return "Không có dữ liệu nhập-xuất-tồn trong kỳ " + fromDate + " đến " + toDate + ".";
        String result = data.stream()
                .map(row -> String.format("- SP: %s (%s) | Lô: %s | ĐVT: %s | Đầu kỳ: %s | Nhập: %s | Xuất: %s | Cuối kỳ: %s",
                        row[2], row[1],  // productName, productCode
                        row[5],          // lotCode
                        row[3],          // unit
                        row[7],          // openingQty
                        row[8],          // inboundQty
                        row[9],          // outboundQty
                        row[10]))        // closingQty
                .collect(Collectors.joining("\n"));
        return "Báo cáo nhập-xuất-tồn từ " + fromDate + " đến " + toDate + " (" + data.size() + " dòng):\n" + result;
    }

    @Tool("Lấy tổng số lượng đã xuất kho theo từng sản phẩm trong 30 ngày gần nhất, dùng để đánh giá tốc độ bán hàng")
    public String getOutboundVelocity() {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        List<Object[]> data = inventoryTransactionRepository.sumOutboundQtyByProductSince(since);
        if (data.isEmpty()) return "Không có dữ liệu xuất kho trong 30 ngày gần nhất.";
        String result = data.stream()
                .map(row -> {
                    Integer productId = (Integer) row[0];
                    Number totalOutQty = (Number) row[1];
                    ProductEntity product = productRepository.findById(productId).orElse(null);
                    String productName = product != null ? product.getName() : "ID=" + productId;
                    return String.format("- %s (Mã: %s): Đã xuất = %s",
                            productName,
                            product != null ? product.getCode() : "N/A",
                            totalOutQty);
                })
                .collect(Collectors.joining("\n"));
        return "Tốc độ xuất kho 30 ngày gần nhất:\n" + result;
    }

    // ==================== TỔNG QUAN HỆ THỐNG ====================

    @Tool("Lấy tổng quan toàn bộ hệ thống kho hàng: tổng sản phẩm, nhà kho, nhà cung cấp, khách hàng, lô hàng, phiếu nhập, phiếu xuất, phiếu kiểm kê, phiếu điều chuyển")
    public String getSystemOverview() {
        return String.format(
                "Tổng quan hệ thống StockFlow:\n" +
                "- Sản phẩm: %d\n" +
                "- Nhà kho: %d\n" +
                "- Danh mục: %d\n" +
                "- Nhà cung cấp: %d\n" +
                "- Khách hàng: %d\n" +
                "- Lô hàng: %d\n" +
                "- Phiếu nhập kho: %d\n" +
                "- Phiếu xuất kho: %d\n" +
                "- Phiếu kiểm kê: %d\n" +
                "- Phiếu điều chuyển: %d",
                productRepository.count(),
                warehouseRepository.count(),
                categoryRepository.count(),
                supplierRepository.count(),
                customerRepository.count(),
                lotRepository.count(),
                inboundRepository.count(),
                outboundRepository.count(),
                stocktakeRepository.count(),
                transferRepository.count()
        );
    }

    // ==================== TÌM KIẾM CHI TIẾT (TOOLS MỚI) ====================

    @Tool("Tìm kiếm sản phẩm theo tên hoặc mã sản phẩm (từ khoá không phân biệt hoa thường). Dùng khi khách hàng hỏi tìm 1 mặt hàng cụ thể.")
    public String searchProducts(String keyword) {
        String lowerKw = keyword.toLowerCase();
        List<ProductEntity> products = productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerKw) || p.getCode().toLowerCase().contains(lowerKw))
                .limit(20)
                .toList();
        if (products.isEmpty()) return "Không tìm thấy sản phẩm nào khớp với từ khoá: " + keyword;
        String data = products.stream()
                .map(p -> String.format("- Mã: %s | Tên: %s | ĐVT: %s | Tồn tối thiểu: %s | Trạng thái: %s",
                        p.getCode(), p.getName(), p.getUnit(), p.getMinStock(), p.getStatus()))
                .collect(Collectors.joining("\n"));
        return "Tìm thấy " + products.size() + " sản phẩm:\n" + data;
    }

    @Tool("Tìm kiếm và xem chi tiết một phiếu nhập kho cụ thể dựa vào mã phiếu (ví dụ: IN-0001)")
    public String findInboundByCode(String code) {
        InboundEntity inbound = inboundRepository.findAll().stream()
                .filter(i -> i.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
        if (inbound == null) return "Không tìm thấy phiếu nhập kho có mã: " + code;
        return String.format("Phiếu Nhập %s:\n- Kho: %s\n- Nhà cung cấp: %s\n- Trạng thái: %s\n- Ngày tạo: %s\n- Ghi chú: %s",
                inbound.getCode(), inbound.getWarehouse().getName(), inbound.getSupplier().getName(),
                inbound.getStatus(), inbound.getCreatedAt(), inbound.getNote());
    }

    @Tool("Tìm kiếm và xem chi tiết một phiếu xuất kho cụ thể dựa vào mã phiếu (ví dụ: OUT-0001)")
    public String findOutboundByCode(String code) {
        OutboundEntity outbound = outboundRepository.findAll().stream()
                .filter(o -> o.getCode().equalsIgnoreCase(code))
                .findFirst().orElse(null);
        if (outbound == null) return "Không tìm thấy phiếu xuất kho có mã: " + code;
        return String.format("Phiếu Xuất %s:\n- Loại xuất: %s\n- Kho: %s\n- Khách hàng: %s\n- Trạng thái: %s\n- Ngày tạo: %s\n- Ghi chú: %s",
                outbound.getCode(), outbound.getIssueType(), outbound.getWarehouse().getName(),
                outbound.getCustomer() != null ? outbound.getCustomer().getName() : "Không có",
                outbound.getStatus(), outbound.getCreatedAt(), outbound.getNote());
    }

    @Tool("CẢNH BÁO: Lấy danh sách các sản phẩm đang có số lượng tồn kho thực tế thấp hơn mức tồn kho tối thiểu (cần nhập thêm hàng).")
    public String getLowStockProducts() {
        List<Object[]> stockData = inventoryRepository.sumStockByProduct();
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Object[] row : stockData) {
            Integer productId = (Integer) row[0];
            Number totalQty = (Number) row[1];
            ProductEntity p = productRepository.findById(productId).orElse(null);
            if (p != null && p.getMinStock() != null && totalQty.intValue() < p.getMinStock()) {
                sb.append(String.format("- SP: %s (Mã: %s) | Tồn kho hiện tại: %s | Tối thiểu yêu cầu: %d\n",
                        p.getName(), p.getCode(), totalQty, p.getMinStock()));
                count++;
            }
        }
        if (count == 0) return "Tất cả các sản phẩm đều đang có tồn kho an toàn (không có sản phẩm nào dưới mức tối thiểu).";
        return "CẢNH BÁO - Có " + count + " sản phẩm dưới mức tồn kho tối thiểu:\n" + sb.toString();
    }

    @Tool("Tìm kiếm khách hàng theo tên hoặc số điện thoại (từ khoá).")
    public String searchCustomers(String keyword) {
        String lowerKw = keyword.toLowerCase();
        List<CustomerEntity> list = customerRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(lowerKw) || 
                             (c.getPhone() != null && c.getPhone().contains(keyword)))
                .limit(10)
                .toList();
        if (list.isEmpty()) return "Không tìm thấy khách hàng nào khớp với: " + keyword;
        String data = list.stream()
                .map(c -> String.format("- Khách hàng: %s | SĐT: %s | Địa chỉ: %s", 
                        c.getName(), c.getPhone(), c.getAddress()))
                .collect(Collectors.joining("\n"));
        return "Tìm thấy " + list.size() + " khách hàng:\n" + data;
    }

    @Tool("Tìm kiếm nhà cung cấp (supplier) theo tên, số điện thoại hoặc email (từ khoá).")
    public String searchSuppliers(String keyword) {
        String lowerKw = keyword.toLowerCase();
        List<SupplierEntity> list = supplierRepository.findAll().stream()
                .filter(s -> s.getName().toLowerCase().contains(lowerKw) || 
                             (s.getPhone() != null && s.getPhone().contains(keyword)) ||
                             (s.getEmail() != null && s.getEmail().toLowerCase().contains(lowerKw)))
                .limit(10)
                .toList();
        if (list.isEmpty()) return "Không tìm thấy nhà cung cấp nào khớp với: " + keyword;
        String data = list.stream()
                .map(s -> String.format("- NCC: %s | Người liên hệ: %s | SĐT: %s | Email: %s", 
                        s.getName(), s.getContactPerson(), s.getPhone(), s.getEmail()))
                .collect(Collectors.joining("\n"));
        return "Tìm thấy " + list.size() + " nhà cung cấp:\n" + data;
    }

    @Tool("Đếm số lượng phiếu xuất kho (outbound) theo trạng thái cụ thể (như: PENDING, COMPLETED, CANCELLED, v.v.). Thường dùng để xem có bao nhiêu đơn đang chờ xuất.")
    public String countOutboundsByStatus(String status) {
        long count = outboundRepository.findAll().stream()
                .filter(o -> o.getStatus() != null && o.getStatus().name().equalsIgnoreCase(status))
                .count();
        return "Có tổng cộng " + count + " phiếu xuất kho đang ở trạng thái: " + status.toUpperCase();
    }

    @Tool("Đếm số lượng phiếu nhập kho (inbound) theo trạng thái cụ thể (như: PENDING, COMPLETED, CANCELLED, v.v.). Thường dùng để xem có bao nhiêu đơn đang chờ nhập.")
    public String countInboundsByStatus(String status) {
        long count = inboundRepository.findAll().stream()
                .filter(i -> i.getStatus() != null && i.getStatus().name().equalsIgnoreCase(status))
                .count();
        return "Có tổng cộng " + count + " phiếu nhập kho đang ở trạng thái: " + status.toUpperCase();
    }

    // ==================== BỘ 20 TOOLS BỔ SUNG (NÂNG CAO) ====================

    @Tool("Lấy danh sách các sản phẩm đang hết sạch hàng trong kho (tồn kho = 0).")
    public String getOutOfStockProducts() {
        List<Object[]> stockData = inventoryRepository.sumStockByProduct();
        List<Integer> productsWithStock = stockData.stream().map(row -> (Integer) row[0]).toList();
        List<ProductEntity> allProducts = productRepository.findAll();
        List<ProductEntity> outOfStock = allProducts.stream()
                .filter(p -> !productsWithStock.contains(p.getId()))
                .limit(20)
                .toList();
        if (outOfStock.isEmpty()) return "Tuyệt vời, không có sản phẩm nào bị hết hàng.";
        return "Các sản phẩm đang hết hàng (tồn kho = 0):\n" + outOfStock.stream()
                .map(p -> "- " + p.getName() + " (Mã: " + p.getCode() + ")").collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách các lô hàng (lot) ĐÃ HẾT HẠN sử dụng tính đến ngày hôm nay.")
    public String getExpiredLots() {
        List<LotEntity> lots = lotRepository.findByStatusAndExpDateBefore(StatusEnum.ACTIVE, LocalDate.now());
        if (lots.isEmpty()) return "Không có lô hàng nào đã hết hạn.";
        return "Danh sách lô ĐÃ HẾT HẠN:\n" + lots.stream()
                .map(l -> "- Lô: " + l.getLotCode() + " | SP: " + l.getProduct().getName() + " | HSD: " + l.getExpDate())
                .collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách các phiếu nhập kho đang ở trạng thái PENDING (chờ duyệt).")
    public String getPendingInbounds() {
        List<InboundEntity> list = inboundRepository.findAll().stream()
                .filter(i -> i.getStatus() != null && i.getStatus().name().equalsIgnoreCase("PENDING"))
                .limit(20).toList();
        if (list.isEmpty()) return "Không có phiếu nhập kho nào đang chờ duyệt.";
        return "Phiếu nhập PENDING:\n" + list.stream().map(i -> "- " + i.getCode() + " (NCC: " + i.getSupplier().getName() + ")").collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách các phiếu xuất kho đang ở trạng thái PENDING (chờ duyệt).")
    public String getPendingOutbounds() {
        List<OutboundEntity> list = outboundRepository.findAll().stream()
                .filter(o -> o.getStatus() != null && o.getStatus().name().equalsIgnoreCase("PENDING"))
                .limit(20).toList();
        if (list.isEmpty()) return "Không có phiếu xuất kho nào đang chờ duyệt.";
        return "Phiếu xuất PENDING:\n" + list.stream().map(o -> "- " + o.getCode() + " (KH: " + (o.getCustomer() != null ? o.getCustomer().getName() : "N/A") + ")").collect(Collectors.joining("\n"));
    }

    @Tool("Tìm kiếm danh mục sản phẩm (category) theo tên.")
    public String searchCategories(String keyword) {
        String kw = keyword.toLowerCase();
        List<CategoryEntity> list = categoryRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(kw)).limit(10).toList();
        if (list.isEmpty()) return "Không tìm thấy danh mục: " + keyword;
        return "Danh mục tìm thấy:\n" + list.stream().map(c -> "- " + c.getName()).collect(Collectors.joining("\n"));
    }

    @Tool("Tìm kiếm Lô hàng (Lot) theo mã lô.")
    public String searchLots(String keyword) {
        String kw = keyword.toLowerCase();
        List<LotEntity> list = lotRepository.findAll().stream()
                .filter(l -> l.getLotCode().toLowerCase().contains(kw)).limit(10).toList();
        if (list.isEmpty()) return "Không tìm thấy mã lô: " + keyword;
        return "Lô tìm thấy:\n" + list.stream().map(l -> "- " + l.getLotCode() + " | SP: " + l.getProduct().getName()).collect(Collectors.joining("\n"));
    }

    @Tool("Lấy tổng số lượng tất cả các sản phẩm đang có trong kho (tính tổng số cái/hộp/...).")
    public String countTotalInventoryQuantity() {
        List<Object[]> stockData = inventoryRepository.sumStockByProduct();
        long total = 0;
        for (Object[] row : stockData) {
            Number qty = (Number) row[1];
            total += qty.longValue();
        }
        return "Tổng số lượng hàng hoá (tất cả chủng loại) đang lưu trữ trong hệ thống là: " + total;
    }

    @Tool("Tìm kiếm và xem chi tiết phiếu điều chuyển kho (Transfer) theo mã phiếu.")
    public String searchTransfersByCode(String code) {
        TransferEntity entity = transferRepository.findAll().stream()
                .filter(t -> t.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (entity == null) return "Không tìm thấy phiếu điều chuyển: " + code;
        return String.format("Phiếu Điều Chuyển %s:\n- Kho: %s\n- Trạng thái: %s\n- Ngày tạo: %s",
                entity.getCode(), entity.getWarehouse().getName(), entity.getStatus(), entity.getCreatedAt());
    }

    @Tool("Tìm kiếm và xem chi tiết phiếu kiểm kê (Stocktake) theo mã phiếu.")
    public String searchStocktakesByCode(String code) {
        StocktakeEntity entity = stocktakeRepository.findAll().stream()
                .filter(s -> s.getCode().equalsIgnoreCase(code)).findFirst().orElse(null);
        if (entity == null) return "Không tìm thấy phiếu kiểm kê: " + code;
        return String.format("Phiếu Kiểm Kê %s:\n- Kho: %s\n- Trạng thái: %s\n- Ngày tạo: %s",
                entity.getCode(), entity.getWarehouse().getName(), entity.getStatus(), entity.getCreatedAt());
    }

    @Tool("Đếm số lượng danh mục con của một danh mục cha.")
    public String countChildrenOfCategory(int parentId) {
        long count = categoryRepository.findByParentId(parentId).size();
        return "Danh mục ID=" + parentId + " có " + count + " danh mục con.";
    }

    @Tool("Liệt kê các sản phẩm chưa được phân vào bất kỳ danh mục nào (Uncategorized).")
    public String getUncategorizedProducts() {
        List<ProductEntity> list = productRepository.findAll().stream()
                .filter(p -> p.getCategory() == null).limit(20).toList();
        if (list.isEmpty()) return "Tất cả sản phẩm đều đã được phân danh mục.";
        return "Sản phẩm chưa có danh mục:\n" + list.stream().map(p -> "- " + p.getName()).collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách các nhà cung cấp đang bị khoá hoặc ngưng hoạt động (Trạng thái INACTIVE).")
    public String getInactiveSuppliers() {
        List<SupplierEntity> list = supplierRepository.findAll().stream()
                .filter(s -> s.getStatus() == StatusEnum.INACTIVE).limit(10).toList();
        if (list.isEmpty()) return "Không có nhà cung cấp nào đang bị khoá.";
        return "Nhà cung cấp INACTIVE:\n" + list.stream().map(s -> "- " + s.getName()).collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách các khách hàng đang bị khoá (Trạng thái INACTIVE).")
    public String getInactiveCustomers() {
        List<CustomerEntity> list = customerRepository.findAll().stream()
                .filter(c -> c.getStatus() == StatusEnum.INACTIVE).limit(10).toList();
        if (list.isEmpty()) return "Không có khách hàng nào đang bị khoá.";
        return "Khách hàng INACTIVE:\n" + list.stream().map(c -> "- " + c.getName()).collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách 5 sản phẩm được tạo gần đây nhất trong hệ thống.")
    public String getNewestProducts() {
        List<ProductEntity> list = productRepository.findAll().stream()
                .sorted((a,b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5).toList();
        return "5 sản phẩm mới nhất:\n" + list.stream().map(p -> "- " + p.getName() + " (Tạo lúc: " + p.getCreatedAt() + ")").collect(Collectors.joining("\n"));
    }

    @Tool("Lấy thời gian hiện tại của hệ thống máy chủ.")
    public String getSystemTime() {
        return "Thời gian hiện tại của máy chủ là: " + LocalDateTime.now().toString();
    }

    @Tool("Lấy thông tin chi tiết của một vị trí kho dựa vào mã vị trí (ví dụ: A-01-01).")
    public String getStorageLocationDetailsByCode(String locationCode) {
        StorageLocationEntity loc = storageLocationRepository.findAll().stream()
                .filter(s -> s.getLocationCode().equalsIgnoreCase(locationCode)).findFirst().orElse(null);
        if (loc == null) return "Không tìm thấy vị trí kho: " + locationCode;
        return String.format("Vị trí %s:\n- Kho: %s\n- Sức chứa (m3): %s\n- Trạng thái: %s",
                loc.getLocationCode(), loc.getWarehouse().getName(), loc.getCapacity(), loc.getStatus());
    }

    @Tool("Thống kê số lượng phiếu nhập theo từng loại trạng thái (PENDING, COMPLETED, CANCELLED).")
    public String getInboundStatusSummary() {
        long pending = inboundRepository.findAll().stream().filter(i -> i.getStatus().name().equals("PENDING")).count();
        long completed = inboundRepository.findAll().stream().filter(i -> i.getStatus().name().equals("COMPLETED")).count();
        long cancelled = inboundRepository.findAll().stream().filter(i -> i.getStatus().name().equals("CANCELLED")).count();
        return String.format("Thống kê phiếu nhập:\n- Chờ duyệt (PENDING): %d\n- Đã xong (COMPLETED): %d\n- Đã huỷ (CANCELLED): %d", pending, completed, cancelled);
    }

    @Tool("Thống kê số lượng phiếu xuất theo từng loại trạng thái (PENDING, COMPLETED, CANCELLED).")
    public String getOutboundStatusSummary() {
        long pending = outboundRepository.findAll().stream().filter(o -> o.getStatus().name().equals("PENDING")).count();
        long completed = outboundRepository.findAll().stream().filter(o -> o.getStatus().name().equals("COMPLETED")).count();
        long cancelled = outboundRepository.findAll().stream().filter(o -> o.getStatus().name().equals("CANCELLED")).count();
        return String.format("Thống kê phiếu xuất:\n- Chờ duyệt (PENDING): %d\n- Đã xong (COMPLETED): %d\n- Đã huỷ (CANCELLED): %d", pending, completed, cancelled);
    }

    @Tool("Lấy danh sách 5 phiếu nhập kho gần đây nhất có trạng thái ĐÃ HUỶ (CANCELLED).")
    public String getRecentCancelledInbounds() {
        List<InboundEntity> list = inboundRepository.findAll().stream()
                .filter(i -> i.getStatus().name().equals("CANCELLED"))
                .sorted((a,b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5).toList();
        if (list.isEmpty()) return "Không có phiếu nhập nào bị huỷ gần đây.";
        return "5 Phiếu nhập ĐÃ HUỶ gần nhất:\n" + list.stream().map(i -> "- " + i.getCode()).collect(Collectors.joining("\n"));
    }

    @Tool("Lấy danh sách 5 phiếu xuất kho gần đây nhất có trạng thái ĐÃ HUỶ (CANCELLED).")
    public String getRecentCancelledOutbounds() {
        List<OutboundEntity> list = outboundRepository.findAll().stream()
                .filter(i -> i.getStatus().name().equals("CANCELLED"))
                .sorted((a,b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .limit(5).toList();
        if (list.isEmpty()) return "Không có phiếu xuất nào bị huỷ gần đây.";
        return "5 Phiếu xuất ĐÃ HUỶ gần nhất:\n" + list.stream().map(i -> "- " + i.getCode()).collect(Collectors.joining("\n"));
    }

    @Tool("Đếm số lượng tài khoản admin hiện tại và liệt kê thông tin chi tiết của họ.")
    public String getAdminAccounts() {
        List<UserEntity> admins = userRepository.findByRole(RoleEnum.ADMIN);
        if (admins.isEmpty()) {
            return "Hiện tại không có tài khoản ADMIN nào trong hệ thống.";
        }
        String data = admins.stream()
                .map(u -> String.format("- ID: %d | Họ tên: %s | Email: %s | SĐT: %s | Trạng thái: %s",
                        u.getId(), u.getFullName(), u.getEmail(), 
                        u.getPhone() != null ? u.getPhone() : "N/A", 
                        u.getIsActive() ? "Đang hoạt động" : "Bị khoá"))
                .collect(Collectors.joining("\n"));
        return "Hệ thống đang có tổng cộng " + admins.size() + " tài khoản ADMIN. Thông tin chi tiết:\n" + data;
    }
}


