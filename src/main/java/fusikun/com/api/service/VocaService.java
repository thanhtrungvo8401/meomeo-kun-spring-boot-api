package fusikun.com.api.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import fusikun.com.api.dao.VocaRepository;
import fusikun.com.api.model.study.Voca;
import fusikun.com.api.specificationSearch.Specification_Voca;

@Service
public class VocaService {
	@Autowired
	VocaRepository vocaRepository;

	public Voca findById(UUID id) {
		Optional<Voca> optVoca = vocaRepository.findById(id);
		if (optVoca.isPresent())
			return optVoca.get();
		else
			return null;
	}

	public Voca save(Voca entity) {
		if (entity.getId() == null) {
			entity.setCreatedDate(new Date());
		}
		entity.setUpdatedDate(new Date());
		if (entity.getIsActive() == null) {
			entity.setIsActive(true);
		}
		return vocaRepository.save(entity);
	}

	public List<Voca> findAll(Specification_Voca specification, Pageable pageable) {
		Page<Voca> page = vocaRepository.findAll(specification, pageable);
		if (page.hasContent())
			return page.getContent();
		return new ArrayList<Voca>();

	}

	public Long count(Specification_Voca specification) {
		return vocaRepository.count(specification);
	}
}