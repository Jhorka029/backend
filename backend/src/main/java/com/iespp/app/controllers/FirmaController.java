package com.iespp.app.controllers;

import com.iespp.app.models.User;
import com.iespp.app.repositories.UserRepository;
import com.iespp.app.services.DigitalFirmaService;
import com.iespp.app.services.PdfFirmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reporte")
public class FirmaController {

    @Autowired
    private DigitalFirmaService firmaService;

    @Autowired
    private PdfFirmaService pdfFirmaService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/firmar")
    public ResponseEntity<?> firmarReporte(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String documentoHash = request.getOrDefault("documentoHash", "");
            if (documentoHash.isEmpty()) {
                documentoHash = java.util.UUID.randomUUID().toString();
            }

            String nombre = user.getNombreCompleto();
            String rol = user.getRoles().stream()
                .findFirst()
                .map(r -> r.getNombre())
                .orElse("Usuario");

            Map<String, Object> firma = firmaService.firmarDocumento(documentoHash, nombre, email, rol);
            return ResponseEntity.ok(firma);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al firmar: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/verificar")
    public ResponseEntity<?> verificarFirma(@RequestBody Map<String, String> request) {
        try {
            String documentoHash = request.get("documentoHash");
            String signature = request.get("signature");
            String signedAt = request.get("signedAt");
            String signerEmail = request.get("signerEmail");

            boolean valida = firmaService.verificarFirma(documentoHash, signature, signedAt, signerEmail);
            Map<String, Object> result = new HashMap<>();
            result.put("valida", valida);
            result.put("mensaje", valida ? "Firma digital válida y auténtica" : "Firma digital inválida o alterada");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al verificar: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @GetMapping("/certificado")
    public ResponseEntity<?> getCertificado() {
        try {
            return ResponseEntity.ok(firmaService.getInformacionCertificado());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al obtener certificado: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @PostMapping("/firmar-pdf")
    public ResponseEntity<?> firmarPdf(@RequestBody Map<String, String> request, Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            String pdfBase64 = request.get("pdfBase64");
            if (pdfBase64 == null || pdfBase64.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "PDF no proporcionado"));
            }

            byte[] pdfBytes = Base64.getDecoder().decode(pdfBase64);
            byte[] signedPdf = pdfFirmaService.firmarPdf(pdfBytes, user);

            String signedBase64 = Base64.getEncoder().encodeToString(signedPdf);
            return ResponseEntity.ok(Map.of("pdfFirmado", signedBase64));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al firmar PDF: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}
