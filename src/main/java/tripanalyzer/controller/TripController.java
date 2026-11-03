package tripanalyzer.controller;

import tripanalyzer.model.VehiclePush;
import tripanalyzer.model.VehiclePushAnalysis;
import tripanalyzer.service.TripAnalysisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class TripController {
    private final TripAnalysisService service;

    @PostMapping("/trip")
    public VehiclePushAnalysis analyze(@RequestBody VehiclePush input) {
        return service.analyze(input);
    }
}
