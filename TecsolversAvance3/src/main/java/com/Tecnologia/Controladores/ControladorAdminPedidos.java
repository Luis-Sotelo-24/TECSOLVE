package com.Tecnologia.Controladores;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.Tecnologia.Modelo.Clientes;
import com.Tecnologia.Modelo.Pedido;
import com.Tecnologia.Repositorio.ClientesRepositorio;
import com.Tecnologia.Repositorio.PedidoRepositorio;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.List;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/adminventas")
public class ControladorAdminPedidos {

    @Autowired
    private PedidoRepositorio pedidoRepository;

    @Autowired
    private ClientesRepositorio clienteRepository;

    @GetMapping
    public String listarPedidosFull(HttpSession session, Model model) {
        Clientes cliente = (Clientes) session.getAttribute("cliente");
        if (cliente != null) {
            List<Pedido> pedidos = pedidoRepository.findAll();
            model.addAttribute("listaPedidosFull", pedidos);
            return "AdminPedidos";
        } else {
            return "redirect:/login";
        }
    }

    @PostMapping("/guardar")
    public String guardarPedidos(@ModelAttribute Pedido pedidoForm, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pedido", pedidoForm);
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("modalError", true);
            return "adminproductos";
        }

        Pedido pedidoOriginal = pedidoRepository.findById(pedidoForm.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("ID inválido: " + pedidoForm.getIdPedido()));

        pedidoOriginal.setEstado(pedidoForm.getEstado());

        pedidoRepository.save(pedidoOriginal);

        return "redirect:/adminventas";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPedido(@PathVariable Long id) {
        pedidoRepository.deleteById(id);
        return "redirect:/adminventas";
    }

    @GetMapping("/exportarPedidosExcel")
    public void exportarPedidosExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=pedidos.xlsx");

        List<Pedido> pedidos = pedidoRepository.findAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Pedidos");

        // Crear estilos
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.cloneStyleFrom(dataStyle);
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.cloneStyleFrom(dataStyle);
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));

        // Cabecera
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Cliente", "Fecha", "Estado", "Total"};

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Datos
        int rowNum = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);

            Cell cell0 = row.createCell(0);
            cell0.setCellValue(pedido.getIdPedido());
            cell0.setCellStyle(dataStyle);

            Cell cell1 = row.createCell(1);
            cell1.setCellValue(pedido.getCliente().getNombreCli());
            cell1.setCellStyle(dataStyle);

            Cell cell2 = row.createCell(2);
            cell2.setCellValue(pedido.getFecha());
            cell2.setCellStyle(dateStyle);

            Cell cell3 = row.createCell(3);
            cell3.setCellValue(pedido.getEstado());
            cell3.setCellStyle(dataStyle);

            Cell cell4 = row.createCell(4);
            cell4.setCellValue(pedido.getTotal().doubleValue());
            cell4.setCellStyle(currencyStyle);
        }

        // Ajustar columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Escribir al response
        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
