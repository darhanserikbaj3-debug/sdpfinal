package com.example.sdpfinal.minecraft.builder;

import com.example.sdpfinal.minecraft.model.*;

public record AnvilOrder(String itemId, String sacrificeId, int materialUnits, String name) {
    public static class Builder {

        private String itemId;
        private String sacrificeId;
        private String name;
        private int materialUnits;

        public Builder target(String id) {
            if (id == null || id.isBlank()) {
                throw new WorkshopException("Choose a target item");
            }
            itemId = id;
            return this;
        }

        public Builder material(int units) {
            if (units < 0 || units > 64) {
                throw new WorkshopException("Use 0 to 64 material units");
            }
            materialUnits = units;
            return this;
        }

        public Builder sacrifice(String id) {
            if (id == null || id.isBlank()) {
                sacrificeId = null;
            } else {
                sacrificeId = id;
            }
            return this;
        }

        public Builder rename(String value) {
            if (value != null) {
                value = value.strip();
                if (value.isEmpty() || value.codePointCount(0, value.length()) > 50) {
                    throw new WorkshopException("Name must contain 1 to 50 characters");
                }
            }
            if (value != null) {
                for (int i = 0; i < value.length(); i++) {
                    if (Character.isISOControl(value.charAt(i))) {
                        throw new WorkshopException("Name must contain 1 to 50 characters");
                    }
                }
            }
            name = value;
            return this;
        }

        public AnvilOrder build() {
            if (itemId == null) {
                throw new WorkshopException("Choose a target item");
            }
            if (itemId.equals(sacrificeId)) {
                throw new WorkshopException("An item cannot be combined with itself");
            }
            if (materialUnits > 0 && sacrificeId != null) {
                throw new WorkshopException("Choose material or a second item");
            }
            if (materialUnits == 0 && sacrificeId == null && name == null) {
                throw new WorkshopException("Add material, a second item or a new name");
            }
            return new AnvilOrder(itemId, sacrificeId, materialUnits, name);
        }
    }
}
