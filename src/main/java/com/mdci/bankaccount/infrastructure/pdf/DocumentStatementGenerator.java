package com.mdci.bankaccount.infrastructure.pdf;

import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.application.dto.OperationDTO;
import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import com.mdci.bankaccount.application.port.out.DocumentGenerator;

public class DocumentStatementGenerator implements DocumentGenerator<AccountStatementDTO> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public byte[] generate(AccountStatementDTO dto) {
        try (PDDocument doc = new PDDocument(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);

            float y = 750;

            // Header
            content.beginText();
            content.setFont(PDType1Font.HELVETICA_BOLD, 14);
            content.newLineAtOffset(50, y);
            content.showText("Relevé de compte bancaire");
            content.endText();

            content.setFont(PDType1Font.HELVETICA, 11);
            y -= 30;
            y = writeLine(content, y, "ID du compte : " + dto.accountId());
            y = writeLine(content, y, "Type de compte : " + dto.accountType());
            y = writeLine(content, y, "Solde actuel : " + dto.currentBalance() + " EUR");
            y = writeLine(content, y, "Période : du " + dto.from().format(DATE_FORMAT) + " au " + dto.to().format(DATE_FORMAT));
            y = writeLine(content, y, "Émis le : " + dto.issuedAt().format(DATE_FORMAT));
            y -= 10;

            // En-tête du tableau
            content.setFont(PDType1Font.HELVETICA_BOLD, 10);
            float[] colWidths = {80, 80, 80, 100, 100};
            float tableStartX = 50;
            writeRow(content, y, colWidths, new String[]{"Date", "Type", "Montant (EUR)", "ID Opération", "Solde après (EUR)"}, tableStartX, true);
            y -= 15;

            // Données
            content.setFont(PDType1Font.HELVETICA, 10);
            double balance = 0;
            double totalDeposit = 0;
            double totalWithdrawal = 0;

            List<OperationDTO> sorted = dto.recentOperations().stream()
                    .sorted(Comparator.comparing(OperationDTO::date))
                    .toList();

            for (OperationDTO op : sorted) {
                if ("DEPOSIT".equals(op.type())) {
                    balance += op.amount().doubleValue();
                    totalDeposit += op.amount().doubleValue();
                } else if ("WITHDRAWAL".equals(op.type())) {
                    balance -= op.amount().doubleValue();
                    totalWithdrawal += op.amount().doubleValue();
                }

                String[] row = {
                        op.date().format(DATE_FORMAT),
                        op.type(),
                        String.format("%.2f", op.amount()),
                        op.operationId().toString().substring(0, 8),
                        String.format("%.2f", balance)
                };
                writeRow(content, y, colWidths, row, tableStartX, false);
                y -= 15;
                if (y < 100) {
                    content.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    y = 750;
                }
            }

            // Récap
            y -= 20;
            content.setFont(PDType1Font.HELVETICA_BOLD, 12);
            y = writeLine(content, y, "RÉCAPITULATIF");
            content.setFont(PDType1Font.HELVETICA, 11);
            y = writeLine(content, y, "Total dépôts    : " + String.format("%.2f EUR", totalDeposit));
            y = writeLine(content, y, "Total retraits  : " + String.format("%.2f EUR", totalWithdrawal));
            y = writeLine(content, y, "Solde net       : " + String.format("%.2f EUR", totalDeposit - totalWithdrawal));

            content.close();
            doc.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erreur PDF", e);
        }
    }

    private static float writeLine(PDPageContentStream content, float y, String text) throws IOException {
        content.beginText();
        content.newLineAtOffset(50, y);
        content.showText(text);
        content.endText();
        return y - 15;
    }

    private static void writeRow(PDPageContentStream content, float y, float[] colWidths, String[] values, float startX, boolean bold) throws IOException {
        float x = startX;
        content.beginText();
        content.newLineAtOffset(x, y);
        for (int i = 0; i < values.length; i++) {
            content.showText(values[i]);
            x += colWidths[i];
            content.endText();
            if (i < values.length - 1) {
                content.beginText();
                content.newLineAtOffset(x, y);
            }
        }
    }
}
