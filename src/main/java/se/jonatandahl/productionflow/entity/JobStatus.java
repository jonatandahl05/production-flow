package se.jonatandahl.productionflow.entity;

public enum JobStatus {
    CREATED,
    PRODUCTION_READY,
    PRINTING,
    PRINTED,
    REWINDING,
    COMPLETED
}