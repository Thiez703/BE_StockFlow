package com.vertex.stockflow.service;

import com.vertex.stockflow.common.enums.StatusEnum;
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
}
