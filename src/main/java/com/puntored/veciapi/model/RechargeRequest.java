package com.puntored.veciapi.model;

public record RechargeRequest(String cellPhone, int value, String supplierId) {
}