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
		AttractionResponse attraction = attractionService.getAttraction(id);
		return new ResponseEntity<>(attraction, HttpStatus.OK);
	}

	@GetMapping
	public ResponseEntity<List<AttractionResponse>> getAllAttractions() {
		List<AttractionResponse> attractions = attractionService.getAllAttractions();
		return new ResponseEntity<>(attractions, HttpStatus.OK);
	}
	
	@PatchMapping("/{id}")
	public ResponseEntity<?> updateAttraction(@PathVariable int id, @RequestBody Attraction attraction) {
		attraction.setAttractionId(id);
        AttractionResponse updatedAttraction  = attractionService.updateAttraction(attraction);
		return new ResponseEntity<>(updatedAttraction, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteAttraction(@PathVariable int id) {
		AttractionResponse deletedAttraction = attractionService.deleteAttraction(id);
        return new ResponseEntity<>(deletedAttraction, HttpStatus.OK);
	}
}
