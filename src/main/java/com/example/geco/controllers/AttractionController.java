package com.example.geco.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.geco.domains.Attraction;
import com.example.geco.dto.AttractionResponse;

@RestController
@RequestMapping("/attraction")
public class AttractionController extends AbstractController {
	@PostMapping
	public ResponseEntity<?> addAttraction(@RequestBody Attraction attraction) {
		AttractionResponse savedAttraction = attractionService.addAttraction(attraction);
        return new ResponseEntity<>(savedAttraction, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getAttraction(@PathVariable int id) {
		AttractionResponse savedAttraction = attractionService.getAttraction(id);
		return new ResponseEntity<>(savedAttraction, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<AttractionResponse>> getAllAttractions() {
		List<AttractionResponse> savedAttractions = attractionService.getAllAttractions();
		return new ResponseEntity<>(savedAttractions, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateAttraction(@PathVariable int id, @RequestBody Attraction attraction) {
		attraction.setAttractionId(id);
        AttractionResponse savedAttraction  = attractionService.updateAttraction(attraction);
		return new ResponseEntity<>(savedAttraction, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAttraction(@PathVariable int id) {
		AttractionResponse savedAttraction = attractionService.deleteAttraction(id);
        return new ResponseEntity<>(savedAttraction, HttpStatus.OK);
	}
}
