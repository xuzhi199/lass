package com.aorise.log.service;

import java.util.Map;

import com.aorise.model.common.Page;
import com.aorise.model.log.Log;

public interface LogService {

	/**
	 * 保存日志
	 *
	 * @param log
	 */
	void save(Log log);

	Page<Log> findLogs(Map<String, Object> params);

}
