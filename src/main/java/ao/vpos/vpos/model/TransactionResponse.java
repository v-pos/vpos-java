/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ao.vpos.vpos.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author sergio
 */
final public class Transaction {
    @JsonProperty("id")
    private String  id;
    @JsonProperty("amount")
    private String  amount;
    @JsonProperty("mobile")
    private String  mobile;
    @JsonProperty("parent_transaction_id")
    private String  parentTransactionId;
    @JsonProperty("type")
    private String  type;
    @JsonProperty("pos_id")
    private Integer posId;
    @JsonProperty("supervisor_card")
    private String  supervisorCard;
    @JsonProperty("clearing_period")
    private String  clearingPeriod;
    @JsonProperty("status")
    private String  status;
    @JsonProperty("status_reason")
    private String  statusReason;
    @JsonProperty("status_datetime")
    private String statusDatetime;

    public Transaction() {
    }

    public Transaction(
            String id,
            String amount,
            String mobile,
            String parentTransactionId,
            String type,
            Integer posId,
            String supervisorCard,
            String clearingPeriod,
            String status,
            String statusReason,
            String statusDatetime
    ) {
        this.id = id;
        this.amount = amount;
        this.mobile = mobile;
        this.parentTransactionId = parentTransactionId;
        this.type = type;
        this.posId = posId;
        this.supervisorCard = supervisorCard;
        this.clearingPeriod = clearingPeriod;
        this.status = status;
        this.statusReason = statusReason;
        this.statusDatetime = statusDatetime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public void setParentTransactionId(String parentTransactionId) {
        this.parentTransactionId = parentTransactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPosId() {
        return posId;
    }

    public void setPosId(Integer posId) {
        this.posId = posId;
    }

    public String getSupervisorCard() {
        return supervisorCard;
    }

    public void setSupervisorCard(String supervisorCard) {
        this.supervisorCard = supervisorCard;
    }

    public String getClearingPeriod() {
        return clearingPeriod;
    }

    public void setClearingPeriod(String clearingPeriod) {
        this.clearingPeriod = clearingPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public void setStatusReason(String statusReason) {
        this.statusReason = statusReason;
    }

    public String getStatusDatetime() {
        return statusDatetime;
    }

    public void setStatusDatetime(String statusDatetime) {
        this.statusDatetime = statusDatetime;
    }

    @Override
    public String toString() {
        return String.format("\"id\": \"%s\", \"type\": \"%s\", \"amount\": \"%s\", \"mobile\": \"%s\", \"pos_id\": \"%s\", \"supervisor_card\": \"%s\", \"status\": \"%s\", \"status_reason\": \"%s\", \"status_datetime\": \"%s\", \"clearing_period\": \"%s\", \"parent_transaction_id\": \"%s\"", id, type, amount, mobile, posId, supervisorCard, status, statusReason, statusDatetime, clearingPeriod, parentTransactionId);
    }

}
