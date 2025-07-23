/*
 * Created by Eugene Sokolov 21.06.2024, 12:08.
 */

package org.gs.kcusers.controller.api;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.gs.kcusers.controller.CommonController;
import org.gs.kcusers.domain.Event;
import org.gs.kcusers.repositories.EventRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.gs.kcusers.utils.Utils.formatDate;

@RestController
@RequestMapping("/api/events-xlsx")
public class UserEventsXlsxApiController extends CommonController {
    protected EventRepository eventRepository;

    UserEventsXlsxApiController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    void addCell(Row row, int cellPosition, String content, CellStyle style) {
        Cell cell = row.createCell(cellPosition);
        cell.setCellValue(content);
        if (style != null) cell.setCellStyle(style);
    }

    void addCell(Row row, int cellPosition, String content) {
        addCell(row, cellPosition, content, null);
    }

    @PreAuthorize("hasAnyAuthority(@getUserRoles)")
    @GetMapping("/{realmName}/{userName}")
    public ResponseEntity<byte[]> eventsPage(@PathVariable String realmName, @PathVariable String userName,
                                             @PageableDefault Pageable pagable) {
        saveLoginEvent();

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчёт");
        sheet.setDefaultColumnWidth(50);

        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);

        // Create header row
        Row headerRow = sheet.createRow(0);
        addCell(headerRow, 0, "Создано", style);
        addCell(headerRow, 1, "Комментарий", style);
        addCell(headerRow, 2, "Инициатор", style);
        addCell(headerRow, 3, "Состояние", style);


        List<Event> events = eventRepository.findByUserNameAndRealmNameOrderByCreatedDesc(
                userName,
                realmName);

        AtomicInteger i = new AtomicInteger(1);
        events.forEach(event -> {
            Row dataRow = sheet.createRow(i.getAndIncrement());

            addCell(dataRow, 0, formatDate(event.getCreated()));
            addCell(dataRow, 1, event.getComment());
            addCell(dataRow, 2, event.getAdmLogin());
            addCell(dataRow, 3, event.getEnabled() ? "Включен" : "Заблокирован");
        });
        // Write the workbook to a ByteArrayOutputStream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            workbook.write(outputStream);
            workbook.close(); // Close the workbook after writing

            // Set response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "sample.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
