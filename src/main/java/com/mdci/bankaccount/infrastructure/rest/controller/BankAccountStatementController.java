package com.mdci.bankaccount.infrastructure.rest.controller;

import com.mdci.bankaccount.application.dto.AccountStatementDTO;
import com.mdci.bankaccount.application.mapper.StatementMapper;
import com.mdci.bankaccount.application.port.out.PdfGenerator;
import com.mdci.bankaccount.domain.model.AccountStatement;
import com.mdci.bankaccount.domain.port.in.IBankAccountStatementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Relevés de compte", description = "Endpoints liés aux relevés de compte")
@RestController
@Validated
@RequestMapping("/api/account/{id}/statement")
public class BankAccountStatementController {

    private final IBankAccountStatementService statementService;
    private final StatementMapper statementMapper;
    private final PdfGenerator<AccountStatementDTO> pdfGenerator;

    public BankAccountStatementController(IBankAccountStatementService statementService, StatementMapper statementMapper,
                                          PdfGenerator<AccountStatementDTO> pdfGenerator) {
        this.statementService = statementService;
        this.statementMapper = statementMapper;
        this.pdfGenerator = pdfGenerator;
    }

    @Operation(
            summary = "Obtenir un relevé de compte (JSON)",
            description = "Retourne un relevé avec solde et liste d'opérations sur la période fournie. Dernier mois par défaut.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Relevé JSON", content = @Content(schema = @Schema(implementation = AccountStatementDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Compte introuvable"),
                    @ApiResponse(responseCode = "400", description = "Période invalide")
            }
    )
    @GetMapping
    public ResponseEntity<AccountStatementDTO> getStatement(
            @PathVariable String id,
            @Parameter(description = "Date de début (optionnelle) (format ISO, ex: 2025-03-01T00:00:00)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Date de fin (optionnelle)") @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        AccountStatement statement = statementService.generateStatementForPeriod(id, from, to);

        return ResponseEntity.ok(statementMapper.toDto(statement));
    }

    @Operation(
            summary = "Exporter un relevé de compte au format PDF",
            description = "Génère un relevé pour la période donnée. Si aucune date n’est fournie, le dernier mois est utilisé.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "PDF généré", content = @Content(mediaType = "application/pdf")),
                    @ApiResponse(responseCode = "404", description = "Compte introuvable"),
                    @ApiResponse(responseCode = "400", description = "Paramètres invalides")
            }
    )
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportStatementPdf(
            @PathVariable String id,
            @Parameter(description = "Date de début de la période (format ISO, ex: 2025-03-01T00:00:00)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @Parameter(description = "Date de fin de la période (format ISO)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        AccountStatement statement = statementService.generateStatementForPeriod(id, from, to);
        AccountStatementDTO dto = statementMapper.toDto(statement);

        byte[] pdfBytes = pdfGenerator.generate(dto);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=releve-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}