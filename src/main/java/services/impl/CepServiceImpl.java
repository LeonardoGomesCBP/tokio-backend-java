package services.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import dto.CepDTO;
import exception.InvalidCepException;
import services.CepService;

@Service
public class CepServiceImpl implements CepService {

    private final WebClient webClient;
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/";

    public CepServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(VIA_CEP_URL).build();
    }

    @Override
    public CepDTO getCepInfo(String cep) {
        if (cep == null || cep.isEmpty()) {
            throw new InvalidCepException("CEP não pode ser vazio");
        }
        
        String cleanedCep = cep.replaceAll("\\D", "");
        
        if (cleanedCep.length() != 8) {
            throw new InvalidCepException("O CEP deve ter 8 dígitos: " + cep);
        }
        
        try {
            CepDTO cepInfo = webClient.get()
                .uri("/{cep}/json/", cleanedCep)
                .retrieve()
                .bodyToMono(CepDTO.class)
                .block();
                
            if (cepInfo == null || cepInfo.logradouro() == null || cepInfo.localidade() == null) {
                throw new InvalidCepException(cep);
            }
            
            return cepInfo;
        } catch (WebClientResponseException e) {
            throw new InvalidCepException(cep, e);
        }
    }
} 