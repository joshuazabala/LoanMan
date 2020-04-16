package com.codefaucet.LoanMan.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codefaucet.LoanMan.common.LoggingHelper;
import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.common.PagedSearchResponse;
import com.codefaucet.LoanMan.common.ResponseContainer;
import com.codefaucet.LoanMan.common.StringHelper;
import com.codefaucet.LoanMan.common.Util;
import com.codefaucet.LoanMan.dto.ClientDTO;
import com.codefaucet.LoanMan.model.Client;
import com.codefaucet.LoanMan.service.ClientService;

@RestController
@RequestMapping("/api/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoggingHelper loggingHelper;

    private final Logger logger = LoggerFactory.getLogger(ClientController.class);

    @GetMapping({"/findById", "/findById/{clientId}"})
    public ClientDTO findById(@PathVariable(required = false) String clientId) {
	logger.debug("search | clientId: " + clientId);

	Client client = StringHelper.isNullOrEmpty(clientId) ? new Client() : clientService.findById(clientId);
	ClientDTO dto = new ClientDTO(client.getId(), client.isActive(), client.getFirstName(), client.getMiddleName(),
		client.getLastName(), client.getContactNumber(), client.getEmailAddress(), client.getAddress());
	return dto;
    }

    @GetMapping("/delete/{clientId}")
    public void delete(@PathVariable String clientId) {
	logger.debug("delete | clientId: " + clientId);

	clientService.delete(clientId);
    }

    @GetMapping("/restore/{clientId}")
    public void restore(@PathVariable String clientId) {
	logger.debug("restore | clientId: " + clientId);

	clientService.restore(clientId);
    }

    @PostMapping("/create")
    public ResponseContainer<ClientDTO> create(@RequestBody ClientDTO dto) {
	logger.debug("create | dto: " + loggingHelper.asString(dto));

	ResponseContainer<ClientDTO> response = new ResponseContainer<ClientDTO>();

	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getFirstName())) {
	    response.getErrorMap().put("firstName", "First name can't be empty.");
	}
	if (StringHelper.isNullOrEmpty(dto.getLastName())) {
	    response.getErrorMap().put("lastName", "Last name can't be empty.");
	}
	if (StringHelper.isNullOrEmpty(dto.getContactNumber())) {
	    response.getErrorMap().put("contactNumber", "Contact number can't be empty.");
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

	Client client = new Client(dto.getFirstName(), dto.getMiddleName(), dto.getLastName(), dto.getContactNumber(),
		dto.getEmailAddress(), dto.getAddress());
	client = clientService.create(client);

	dto = new ClientDTO(client.getId(), client.isActive(), client.getFirstName(), client.getMiddleName(),
		client.getLastName(), client.getContactNumber(), client.getEmailAddress(), client.getAddress());
	return response.successful(dto);
    }

    @PostMapping("/edit")
    public ResponseContainer<ClientDTO> edit(@RequestBody ClientDTO dto) {
	logger.debug("edit | dto: " + loggingHelper.asString(dto));

	ResponseContainer<ClientDTO> response = new ResponseContainer<ClientDTO>();

	// basic validation
	if (StringHelper.isNullOrEmpty(dto.getFirstName())) {
	    response.getErrorMap().put("firstName", "First name can't be empty.");
	}
	if (StringHelper.isNullOrEmpty(dto.getLastName())) {
	    response.getErrorMap().put("lastName", "Last name can't be empty.");
	}
	if (StringHelper.isNullOrEmpty(dto.getContactNumber())) {
	    response.getErrorMap().put("contactNumber", "Contact number can't be empty.");
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

	Client client = clientService.findById(dto.getId());
	client.setFirstName(dto.getFirstName());
	client.setMiddleName(dto.getMiddleName());
	client.setLastName(dto.getLastName());
	client.setContactNumber(dto.getContactNumber());
	client.setEmailAddress(dto.getEmailAddress());
	client.setAddress(dto.getAddress());
	client = clientService.create(client);

	dto = new ClientDTO(client.getId(), client.isActive(), client.getFirstName(), client.getMiddleName(),
		client.getLastName(), client.getContactNumber(), client.getEmailAddress(), client.getAddress());
	return response.successful(dto);
    }

    @PostMapping("/search")
    public PagedSearchResponse<ClientDTO> search(@RequestBody PagedSearchRequest param) {
	logger.debug("search | param: " + loggingHelper.asString(param));

	PagedSearchResponse<ClientDTO> response = new PagedSearchResponse<ClientDTO>();
	List<Client> clients = clientService.search(param);
	long totalCount = clientService.countSearchResult(param);

	List<ClientDTO> dtos = new ArrayList<ClientDTO>();
	clients.forEach(item -> {
	    ClientDTO dto = new ClientDTO(item.getId(), item.isActive(), item.getFirstName(), item.getMiddleName(),
		    item.getLastName(), item.getContactNumber(), item.getEmailAddress(), item.getAddress());
	    dtos.add(dto);
	});
	response.setColumnSorting(param.getColumnSorting());

	return response.successful(dtos, Util.getTotalPage(totalCount, param.getPageSize()));
    }

}
