package com.learn.desifans_user_service.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TipMenu {
    
    private List<TipOption> options;
    private String customMessage;
    private boolean enabled;
    
    // Constructors
    public TipMenu() {
        this.options = new ArrayList<>();
        this.enabled = true;
        // Add default tip options
        this.options.add(new TipOption(new BigDecimal("5.00"), "☕ Coffee"));
        this.options.add(new TipOption(new BigDecimal("10.00"), "🍕 Pizza"));
        this.options.add(new TipOption(new BigDecimal("25.00"), "💐 Flowers"));
        this.options.add(new TipOption(new BigDecimal("50.00"), "💎 Premium"));
    }
    
    // Getters and Setters
    public List<TipOption> getOptions() {
        return options;
    }
    
    public void setOptions(List<TipOption> options) {
        this.options = options;
    }
    
    public String getCustomMessage() {
        return customMessage;
    }
    
    public void setCustomMessage(String customMessage) {
        this.customMessage = customMessage;
    }
    
    public boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    // Helper class for tip options
    public static class TipOption {
        private BigDecimal amount;
        private String label;
        
        public TipOption() {}
        
        public TipOption(BigDecimal amount, String label) {
            this.amount = amount;
            this.label = label;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
    }
}
