package com.example.distributed_lovable.intelligence_service.controller;

import com.example.distributed_lovable.intelligence_service.service.UsageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/usage")
public class UsageController {
	
	private final UsageService usageService;
	
	
	@GetMapping("/today")
	public ResponseEntity<Void> getUsageToday(){

//		return ResponseEntity.ok(usageService.checkDailyTokensUsage());
		return null;
	}

	
}
