package com.bruno.dscommerce.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.bruno.dscommerce.Entities.Product;
import com.bruno.dscommerce.Repositories.ProductRepository;
import com.bruno.dscommerce.dto.ProductDTO;
import com.bruno.dscommerce.services.exceptions.ResourceNotFoundException;
import com.bruno.dscommerce.services.exceptions.DatabaseException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private ProductRepository repository;
	
	// buscar produto banco de dados / mensagem de erro
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Optional<Product> result = repository.findById(id);
		Product product = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new ProductDTO(product);
	}
	
	// listar produto
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable) {
		Page<Product> result = repository.findAll(pageable);
		return result.map(x -> new ProductDTO(x));
		
	}
	
	// adicionar produto
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);
		return new ProductDTO(entity);
	}
	
	// atualizar produto por ID
	@Transactional
	public ProductDTO update(Long id, ProductDTO dto) {
		// Atualizaçao de ID que nao existe
		try {
			Product entity = repository.getReferenceById(id);
			copyDtoToEntity(dto, entity);
			entity = repository.save(entity);
			return new ProductDTO(entity);
	}
		catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException("Recurso não encontrado");
	}
}
	
	// deletar Produto por ID
	@Transactional(propagation = Propagation.SUPPORTS )
	public void delete(Long id) {
		// deletar ID não existente / duas vezes o mesmo
		if (!repository.existsById(id)) {
			throw new ResourceNotFoundException("Recurso não encontrado");
	}
		try {
	        	repository.deleteById(id);    		
	}
	    	catch (DataIntegrityViolationException e) {
	        	throw new DatabaseException("Falha de integridade referencial");
	        	
	}
	}
	

	private void copyDtoToEntity(ProductDTO dto, Product entity) {
		entity.setName(dto.getName());
		entity.setDescription(dto.getDescription());
		entity.setPrice(dto.getPrice());
		entity.setImgUrl(dto.getImgUrl());
	
	}

}
