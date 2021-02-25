package com.aorise.backend.service;

import java.util.Map;

import com.aorise.backend.model.BlackIP;
import com.aorise.model.common.Page;

public interface BlackIPService {

	void save(BlackIP blackIP);

	void delete(String ip);

	Page<BlackIP> findBlackIPs(Map<String, Object> params);

}
