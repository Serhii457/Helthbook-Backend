package org.example.healthbook.service;

import lombok.RequiredArgsConstructor;
import org.example.healthbook.dto.MedicalRecordCreateDTO;
import org.example.healthbook.dto.MedicalRecordDTO;
import org.example.healthbook.model.*;
import org.example.healthbook.repository.AppointmentRepository;
import org.example.healthbook.repository.DoctorRepository;
import org.example.healthbook.repository.MedicalRecordRepository;
import org.example.healthbook.repository.PatientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import org.springframework.transaction.annotation.Transactional;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getAllRecords() {
        return medicalRecordRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getRecordsByPatientId(Long patientId) {
        return medicalRecordRepository.findByPatientId(patientId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public MedicalRecordDTO createRecord(MedicalRecordCreateDTO dto, String doctorUsername) {
        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found"));
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                throw new IllegalStateException("Cannot add record to a completed appointment");
            }
        }

        MedicalRecord record = new MedicalRecord();
        record.setDoctor(doctor);
        record.setPatient(patient);
        record.setDiagnosis(dto.getDiagnosis());
        record.setComment(dto.getComment());
        record.setCreatedAt(LocalDateTime.now());

        return toDTO(medicalRecordRepository.save(record));
    }

    @Transactional
    public MedicalRecordDTO updateRecord(Long id, MedicalRecordCreateDTO dto, String doctorUsername) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));

        if (!record.getDoctor().getUser().getUsername().equals(doctorUsername)) {
            throw new RuntimeException("Access denied");
        }

        if (record.getAppointment() != null
                && record.getAppointment().getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot edit record of a completed appointment");
        }

        Doctor doctor = doctorRepository.findByUserUsername(doctorUsername)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        record.setDoctor(doctor);
        record.setPatient(patient);
        record.setDiagnosis(dto.getDiagnosis());
        record.setComment(dto.getComment());

        MedicalRecord saved = medicalRecordRepository.save(record);
        return toDTO(saved);
    }


    @Transactional
    public void deleteRecordById(Long id) {
        MedicalRecord record = medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medical record not found"));

        if (record.getAppointment() != null
                && record.getAppointment().getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException("Cannot delete record of a completed appointment");
        }

        medicalRecordRepository.deleteById(id);
    }

    private MedicalRecordDTO toDTO(MedicalRecord record) {
        MedicalRecordDTO dto = new MedicalRecordDTO();
        dto.setId(record.getId());
        dto.setDiagnosis(record.getDiagnosis());
        dto.setComment(record.getComment());
        dto.setDate(record.getCreatedAt().toLocalDate());
        dto.setDoctorId(record.getDoctor().getId());
        dto.setDoctorName(record.getDoctor().getUser().getFullName());
        dto.setPatientId(record.getPatient().getId());
        dto.setPatientFullName(record.getPatient().getUser().getFullName());
        dto.setPatientPhone(record.getPatient().getUser().getPhone());
        return dto;
    }

    @Transactional(readOnly = true)
    public List<MedicalRecordDTO> getAllRecordsForDoctor(String username) {
        Doctor doctor = doctorRepository.findByUserUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found"));
        List<MedicalRecord> records = medicalRecordRepository.findByDoctorId(doctor.getId());

        return records.stream()
                .sorted(Comparator.comparing(r -> r.getPatient().getUser().getFullName()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordDTO> getAllRecordsForAdmin(Pageable pageable) {
        Page<MedicalRecord> recordsPage = medicalRecordRepository.findAll(pageable);
        List<MedicalRecordDTO> dtoList = recordsPage.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, recordsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public byte[] exportMedicalRecordToPdf(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        List<MedicalRecord> records = medicalRecordRepository.findByAppointmentId(appointmentId);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 70, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            PdfPTable headerTable = new PdfPTable(2);
            headerTable.setWidthPercentage(100);
            headerTable.setWidths(new int[]{1, 4});

            try {
                Image logo = Image.getInstance(getClass().getClassLoader().getResource("static/logo_health_life.png"));
                logo.scaleToFit(80, 80);
                PdfPCell logoCell = new PdfPCell(logo);
                logoCell.setBorder(Rectangle.NO_BORDER);
                logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                headerTable.addCell(logoCell);
            } catch (Exception e) {
                PdfPCell empty = new PdfPCell(new Phrase(""));
                empty.setBorder(Rectangle.NO_BORDER);
                headerTable.addCell(empty);
            }

            PdfPCell titleCell = new PdfPCell(new Phrase(
                    "Receipt\nAppointment #" + appointmentId,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK)
            ));
            titleCell.setBorder(Rectangle.NO_BORDER);
            titleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            headerTable.addCell(titleCell);
            document.add(headerTable);
            document.add(new Paragraph(" "));

            PdfPTable infoTable = new PdfPTable(2);
            infoTable.setWidthPercentage(100);
            infoTable.setWidths(new int[]{3, 7});
            infoTable.setSpacingBefore(10f);
            infoTable.setSpacingAfter(10f);

            infoTable.addCell(getInfoCell("Patient:"));
            infoTable.addCell(getValueCell(appointment.getPatient().getUser().getFullName()));

            infoTable.addCell(getInfoCell("Phone:"));
            infoTable.addCell(getValueCell(appointment.getPatient().getUser().getPhone()));

            infoTable.addCell(getInfoCell("Doctor:"));
            infoTable.addCell(getValueCell(appointment.getDoctor().getUser().getFullName()));

            infoTable.addCell(getInfoCell("Appointment date:"));
            infoTable.addCell(getValueCell(appointment.getDate() + " " + appointment.getTime()));

            infoTable.addCell(getInfoCell("Status:"));
            infoTable.addCell(getValueCell(appointment.getStatus().toString()));

            document.add(infoTable);

            if (records.isEmpty()) {
                document.add(new Paragraph(
                        "No appointments.",
                        FontFactory.getFont(FontFactory.HELVETICA, 12, Font.ITALIC, Color.GRAY)
                ));
            } else {
                PdfPTable table = new PdfPTable(3);
                table.setWidthPercentage(100);
                table.setWidths(new int[]{2, 4, 6});

                table.addCell(getHeaderCell("Date"));
                table.addCell(getHeaderCell("Diagnoz"));
                table.addCell(getHeaderCell("Recommendations"));

                for (MedicalRecord record : records) {
                    table.addCell(getValueCell(record.getCreatedAt().toLocalDate().toString()));
                    table.addCell(getValueCell(record.getDiagnosis()));
                    table.addCell(getValueCell(record.getComment()));
                }

                document.add(table);
            }

            document.add(new Paragraph(" "));

            Paragraph footer = new Paragraph(
                    "Date: " + LocalDate.now() +
                            "\nDoctor: " + appointment.getDoctor().getUser().getFullName(),
                    FontFactory.getFont(FontFactory.HELVETICA, 11, Font.ITALIC, Color.DARK_GRAY)
            );
            footer.setAlignment(Element.ALIGN_RIGHT);
            document.add(footer);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generation PDF", e);
        }
    }

    private PdfPCell getHeaderCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(new Color(64, 64, 64));
        cell.setPadding(6f);
        return cell;
    }

    private PdfPCell getInfoCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(200, 200, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        return cell;
    }

    private PdfPCell getValueCell(String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "—", font));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(new Color(200, 200, 200));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(6f);
        return cell;
    }
}