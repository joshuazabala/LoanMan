package com.codefaucet.LoanMan.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.codefaucet.LoanMan.common.PagedSearchRequest;
import com.codefaucet.LoanMan.model.Client;
import com.codefaucet.LoanMan.repository.IClientRepository;

@Service
public class ClientService {

    @Autowired
    private IClientRepository clientRepository;
    
    public Client save(Client client) {
	return clientRepository.save(client);
    }

    public List<Client> search(PagedSearchRequest param) {
	Sort sort = Sort.by(Order.asc("lastName"));
	return clientRepository.search(param.getQueryString(), param.createStatusFilter(), param.createPageable(sort));
    }

    public long countSearchResult(PagedSearchRequest param) {
	return clientRepository.countSearchResult(param.getQueryString(), param.createStatusFilter());
    }

    public Client findById(String id) {
	return clientRepository.findById(id).get();
    }

    public long getLoanCount(String id) {
	return clientRepository.getLoanCount(id);
    }

    public void deleteById(String id) {
	Client client = clientRepository.findById(id).get();
	clientRepository.delete(client);
    }
    
}
