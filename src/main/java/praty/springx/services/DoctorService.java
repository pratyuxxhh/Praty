package praty.springx.services;

import praty.springx.core.Result;

/**
 * Environment diagnostics. Implemented in a later phase.
 */
public interface DoctorService {

    Result<DoctorReport> check();

    Result<DoctorReport> checkAndFix();

    record DoctorReport(boolean healthy, String summary) {
    }
}
