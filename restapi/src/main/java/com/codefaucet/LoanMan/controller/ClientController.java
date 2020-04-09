package com.codefaucet.LoanMan.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.RequestContainer;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.StringHelper.CharSet;
import com.codefaucet.LoanMan.dto.ClientDTO;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.model.Client;
import com.codefaucet.LoanMan.service.ClientService;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    private final Logger logger = LogManager.getLogger();

    @Autowired
    private LoggingHelper loggingHelper;

    @GetMapping("/testCreateClient")
    public void testCreateClient() {
	Client client = new Client();
	client.setFirstName("Joshua");
	client.setMiddleName("Perez");
	client.setLastName("Zabala");

	client = clientService.save(client);
    }

    @PostMapping("/search")
    public PagedSearchResponse<ClientDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<ClientDTO> response = new PagedSearchResponse<ClientDTO>();
	try {
	    List<Client> clients = clientService.search(param);
	    long totalCount = clientService.countSearchResult(param);

	    List<ClientDTO> dtos = new ArrayList<ClientDTO>();
	    clients.forEach(item -> {
		ClientDTO dto = new ClientDTO(item.getId(), item.getFirstName(), item.getMiddleName(),
			item.getLastName(), item.getContactNumber(), item.getEmailAddress(), item.getAddress());
		dtos.add(dto);
	    });

	    return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
	} catch (Exception ex) {
	    logger.error("search | Error: " + ex.getMessage(), ex);

	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("/findById")
    public ClientDTO findById(@RequestBody Map<String, String> param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	String id = param.get("id");
	Client client = StringHelper.isNullOrEmpty(id) ? new Client() : clientService.findById(id);

	ClientDTO dto = new ClientDTO(client.getId(), client.getFirstName(), client.getMiddleName(),
		client.getLastName(), client.getContactNumber(), client.getEmailAddress(), client.getAddress());
	return dto;
    }
    
    @PostMapping("/save")
    public ResponseContainer<ClientDTO> save(@RequestBody RequestContainer<ClientDTO> param) {
	logger.debug("save | param: " + loggingHelper.asString(param));
	
	ResponseContainer<ClientDTO> response = new ResponseContainer<ClientDTO>();
	try {
	    ClientDTO dto = param.getContent();
	    
	    // basic validation
	    if (StringHelper.isNullOrEmpty(dto.getFirstName())) {
		response.getErrorMap().put("firstName", "First name can't be empty.");
	    }
	    if (StringHelper.isNullOrEmpty(dto.getLastName())) {
		response.getErrorMap().put("lastName", "Last name can't be empty.");
	    }
	    if (StringHelper.isNullOrEmpty(dto.getContactNumber())) {
		response.getErrorMap().put("contactNumber", "Contact number can't be empty.");
	    } else {
		String contactNumber = dto.getContactNumber();
		// if with a +, make sure it's in front
		int plusIndex = contactNumber.lastIndexOf("\\+");
		if (plusIndex > 0) {
		    response.getErrorMap().put("contactNumber", "Invalid contact number format.");
		} else {
		    String trimmedContactNumber = contactNumber.trim().replaceAll("\\+", "");
		    if (!StringHelper.isInCharSet(CharSet.ALPHANUMERIC, trimmedContactNumber)) {
			response.getErrorMap().put("contactNumber", "Invalid contact number format.");
		    }
		}
	    }
	    if (!response.getErrorMap().isEmpty()) {
		return response.failed();
	    }
	    
	    dto.setFirstName(dto.getFirstName());
	    dto.setMiddleName(StringHelper.isNullOrEmpty(dto.getMiddleName()) ? "" : dto.getMiddleName().trim());
	    dto.setLastName(dto.getLastName());
	    dto.setContactNumber(StringHelper.isNullOrEmpty(dto.getContactNumber()) ? "" : dto.getContactNumber().trim());
	    dto.setEmailAddress(StringHelper.isNullOrEmpty(dto.getEmailAddress()) ? "" : dto.getEmailAddress().trim());
	    dto.setAddress(StringHelper.isNullOrEmpty(dto.getAddress()) ? "" : dto.getAddress().trim());
	    
	    Client model = StringHelper.isNullOrEmpty(dto.getId()) ? new Client() : clientService.findById(dto.getId());
	    
	    // map values
	    model.setFirstName(dto.getFirstName());
	    model.setMiddleName(dto.getMiddleName());
	    model.setLastName(dto.getLastName());
	    model.setContactNumber(dto.getContactNumber());
	    model.setEmailAddress(dto.getEmailAddress());
	    model.setAddress(dto.getAddress());
	    model = clientService.save(model);
	    
	    dto.setId(model.getId());
	    return response.successful(dto);
	} catch (Exception ex) {
	    logger.error("save | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }

    @PostMapping("delete")
    public ResponseContainer<Boolean> delete(@RequestBody RequestContainer<String> param) {
	logger.debug("delete | param: " + loggingHelper.asString(param));
	
	ResponseContainer<Boolean> response = new ResponseContainer<Boolean>();
	String id = param.getContent();
	try {
	    long loanCount = clientService.getLoanCount(id);
	    if (loanCount > 0) {
		return response.failed("This client has loans.");
	    }
	    clientService.deleteById(id);

	    return response.successful(true);
	} catch (Exception ex) {
	    logger.error("delete | Error: " + ex.getMessage(), ex);
	    
	    return response.failed(ex.getMessage());
	}
    }
    
}
