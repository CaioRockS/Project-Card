
package io.github.ds.planeja.dominio.cartao;

import io.github.ds.planeja.common.validation.CampoInvalido;
import io.github.ds.planeja.common.validation.ValidationResult;
import io.github.ds.planeja.dominio.cartao.dto.CartaoForm;
import io.github.ds.planeja.dominio.cartao.model.CartaoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CartaoValidator {

    @Autowired
    private CartaoRepository repository;

    public ValidationResult validar(CartaoForm form, UUID id) {
        var result = ValidationResult.novo();

        var cartaoExistente = repository.findByNome(form.nome());

        if (cartaoExistente.isPresent()) {
            CartaoEntity cartao = cartaoExistente.get();

            if (id == null || !cartao.getId().equals(id)) {
                result.add(new CampoInvalido("nome", "Já cadastrado"));
            }
        }

        return result;
    }
}
