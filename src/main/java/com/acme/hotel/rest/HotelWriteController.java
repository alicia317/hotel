package com.acme.hotel.rest;

import com.acme.hotel.rest.HotelDTO.OnCreate;
import com.acme.hotel.service.HotelWriteService;
import com.acme.hotel.service.VersionOutdatedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import static com.acme.hotel.rest.HotelGetController.ID_PATTERN;
import static com.acme.hotel.rest.HotelGetController.REST_PATH;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.PRECONDITION_REQUIRED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;

/**
 * Eine Controller-Klasse.
 *
 * @author Alicia Schleise
 */
@Controller
@Validated
@RequestMapping(REST_PATH)
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings({"ClassFanOutComplexity", "java:S1075"})
public class HotelWriteController {

    public static final String PROBLEM_PATH = "/problem/";
    private static final String VERSIONSNUMMER_FEHLT = "Versionsnummer fehlt";

    private final HotelWriteService service;
    private final HotelMapper mapper;
    private final UriHelper uriHelper;

    /**
     * Einen neuen Hotel-Datensatz anlegen.
     *
     * @param hotelDTO Das Hotelobjekt aus dem eingegangenen Request-Body.
     * @param request Das Request-Objekt, um `Location` im Response-Header zu erstellen.
     * @return Response mit Statuscode 201 oder Statuscode 422, falls Constraints verletzt
     *      sind oder Statuscode 400, falls es syntaktische Fehler im Request-Body
     *      gibt.
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    @Operation(summary = "Einen neues Hotel anlegen", tags = "Neuanlegen")
    @ApiResponse(responseCode = "201", description = "Hotel neu angelegt")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte vorhanden")
    ResponseEntity<Void> post(
        @RequestBody @Validated({Default.class, OnCreate.class}) final HotelDTO hotelDTO,
        final HttpServletRequest request
    )throws URISyntaxException {
            log.debug("post: hotelDTO{}", hotelDTO);

        if (hotelDTO.username() == null || hotelDTO.password() == null) {
            return badRequest().build();
        }

        final var hotelInput = mapper.toHotel(hotelDTO);
        final var hotel = service.create(hotelInput);
        final var baseUri = uriHelper.getBaseUri(request);
        final var location = new URI(baseUri.toString() + '/' + hotel.getId());
        return created(location).build();
//        hotelInput.setUsername(userDTO.username());
//
//        final var hotel = service.create(hotelInput, userDTO.toUserDetails());
//        final var baseUri = uriHelper.getBaseUri(request);
//        final var location = new URI(STR."\{baseUri.toString()}/\{hotel.getId()}");
//        return created(location).build();
        }

    /**
     * Einen vorhandenen Hotel-Datensatz überschreiben.
     *
     * @param id ID des zu aktualisierenden Hotel.
     * @param hotelDTO Das Hotelobjekt aus dem eingegangenen Request-Body.
     * @param version Versionsnummer aus dem Header If-Match
     * @param request Das Request-Objekt, um ggf. die URL für ProblemDetail zu ermitteln
     * @return Response mit Statuscode 204 oder Statuscode 400, falls der JSON-Datensatz syntaktisch nicht korrekt ist
     *      oder 422 falls Constraints verletzt sind
     *      oder 412 falls die Versionsnummer nicht ok ist oder 428 falls die Versionsnummer fehlt.
     */
    @PutMapping(path = "{id:" + ID_PATTERN + "}", consumes = APPLICATION_JSON_VALUE)
    @ResponseStatus(NO_CONTENT)
    @Operation(summary = "Ein Hotel mit neuen Werten aktualisieren", tags = "Aktualisieren")
    @ApiResponse(responseCode = "204", description = "Aktualisiert")
    @ApiResponse(responseCode = "400", description = "Syntaktische Fehler im Request-Body")
    @ApiResponse(responseCode = "404", description = "Hotel nicht vorhanden")
    @ApiResponse(responseCode = "412", description = "Versionsnummer falsch")
    @ApiResponse(responseCode = "422", description = "Ungültige Werte")
    @ApiResponse(responseCode = "428", description = VERSIONSNUMMER_FEHLT)
    ResponseEntity<Void> put(
        @PathVariable final UUID id,
        @RequestBody @Valid final HotelDTO hotelDTO,
        @RequestHeader("If-Match") final Optional<String> version,
        final HttpServletRequest request
    ) {
        log.debug("put: id={}, hotelDTO{}", id, hotelDTO);
        final int versionInt = getVersion(version, request);
        final var hotelInput = mapper.toHotel(hotelDTO);
        final var hotel = service.update(hotelInput, id, versionInt);
        log.debug("put: {}", hotel);
        return noContent().eTag(STR."\"\{hotel.getVersion()}\"").build();
    }

    @SuppressWarnings({"MagicNumber", "RedundantSuppression"})
    private int getVersion(final Optional<String> versionOpt, final HttpServletRequest request) {
        log.trace("getVersion: {}", versionOpt);
        final var versionStr = versionOpt.orElseThrow(() -> new VersionInvalidException(
            PRECONDITION_REQUIRED,
            VERSIONSNUMMER_FEHLT,
            URI.create(request.getRequestURL().toString()))
        );
        if (versionStr.length() < 3 ||
            versionStr.charAt(0) != '"' ||
            versionStr.charAt(versionStr.length() - 1) != '"') {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                "Ungueltiges ETag " + versionStr,
                URI.create(request.getRequestURL().toString())
            );
        }

        final int version;
        try {
            version = Integer.parseInt(versionStr.substring(1, versionStr.length() - 1));
        } catch (final NumberFormatException ex) {
            throw new VersionInvalidException(
                PRECONDITION_FAILED,
                STR."Ungueltiges ETag \{versionStr}",
                URI.create(request.getRequestURL().toString()),
                ex
            );
        }

        log.trace("getVersion: version={}", version);
        return version;
    }

//    @ExceptionHandler
//    ProblemDetail onUsernameExists(
//        final UsernameExistsException ex,
//        final HttpServletRequest request
//    ) {
//        log.debug("onUsernameExists: {}", ex.getMessage());
//        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
//        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
//        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
//        return problemDetail;
//    }

//    @ExceptionHandler
//    ProblemDetail onPasswordInvalid(
//        final PasswordInvalidException ex,
//        final HttpServletRequest request
//    ) {
//        log.debug("onPasswordInvalid: {}", ex.getMessage());
//        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
//        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.CONSTRAINTS.getValue()}"));
//        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
//        return problemDetail;
//    }

    @ExceptionHandler
    ProblemDetail onVersionOutdated(
        final VersionOutdatedException ex,
        final HttpServletRequest request
    ) {
        log.debug("onVersionOutdated: {}", ex.getMessage());
        final var problemDetail = ProblemDetail.forStatusAndDetail(PRECONDITION_FAILED, ex.getMessage());
        problemDetail.setType(URI.create(STR."\{PROBLEM_PATH}\{ProblemType.PRECONDITION.getValue()}"));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));
        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onConstraintViolations(
        final MethodArgumentNotValidException ex,
        final HttpServletRequest request
    ) {
        log.debug("onConstraintViolations: {}", ex.getMessage());

        final var detailMessages = ex.getDetailMessageArguments();
        final var detail = detailMessages == null
            ? "Constraint Violations"
            : ((String) detailMessages[1]).replace(", and ", ", ");
        final var problemDetail = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, detail);
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.CONSTRAINTS.getValue()));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

        return problemDetail;
    }

    @ExceptionHandler
    ProblemDetail onMessageNotReadable(
        final HttpMessageNotReadableException ex,
        final HttpServletRequest request
    ) {
        log.debug("onMessageNotReadable: {}", ex.getMessage());

        final var problemDetail = ProblemDetail.forStatusAndDetail(BAD_REQUEST, ex.getMessage());
        problemDetail.setType(URI.create(PROBLEM_PATH + ProblemType.BAD_REQUEST.getValue()));
        problemDetail.setInstance(URI.create(request.getRequestURL().toString()));

        return problemDetail;
    }
}
