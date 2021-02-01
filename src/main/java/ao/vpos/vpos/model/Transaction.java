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
final public class TransactionResponse {
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

    public TransactionResponse() {
    }

    public TransactionResponse(
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

    public String getAmount() {
        return amount;
    }

    public String getMobile() {
        return mobile;
    }

    public String getParentTransactionId() {
        return parentTransactionId;
    }

    public String getType() {
        return type;
    }

    public Integer getPosId() {
        return posId;
    }

    public String getSupervisorCard() {
        return supervisorCard;
    }

    public String getClearingPeriod() {
        return clearingPeriod;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusReason() {
        return statusReason;
    }

    public String getStatusDatetime() {
        return statusDatetime;
    }

    @Override
    public String toString() {
        return String.format("\"id\": \"%s\", \"type\": \"%s\", \"amount\": \"%s\", \"mobile\": \"%s\", \"pos_id\": \"%s\", \"supervisor_card\": \"%s\", \"status\": \"%s\", \"status_reason\": \"%s\", \"status_datetime\": \"%s\", \"clearing_period\": \"%s\", \"parent_transaction_id\": \"%s\"", id, type, amount, mobile, posId, supervisorCard, status, statusReason, statusDatetime, clearingPeriod, parentTransactionId);
    }

}
