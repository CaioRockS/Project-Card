import { Component, OnInit, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CategoriaService } from '../categoria-service';
import { ValidationErrorResponse } from '../../common/validation/validation-erro-model';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Observable } from 'rxjs';
import { Header } from '../../common/components/header/header';
import { DadosCategoriaForm } from '../dados-categoria';


interface CadastroCategoriaForm{
  nome: FormControl<string>;
}

@Component({
  selector: 'app-cadastro-categoria',
  imports: [ReactiveFormsModule,
    CommonModule,
    RouterModule,
    Header
  ],
  templateUrl: './cadastro-categoria.html',
  styleUrl: './cadastro-categoria.scss',
})
export class CadastroCategoria implements OnInit {

  form!: FormGroup<CadastroCategoriaForm>;
  rotaAtiva = inject(ActivatedRoute);
  service = inject(CategoriaService);
  toast = inject(ToastrService);
  idCartaoEdicao?: string | null;

  ngOnInit(): void{
    this.form = new FormGroup<CadastroCategoriaForm>({
      nome: new FormControl('', {nonNullable: true, validators : Validators.required})
    });

  }


  isFormInvalid() : boolean{
    if(this.form.invalid){
      this.form.markAllAsTouched();
       this.toast.error('Erro de validação. Verifique os valores informados. ');
      return true;
    }
    return false;
  }

  handleSubmit(){
    if(this.isFormInvalid()){
      return;
    }

    const dadosCategoria = this.form.value as DadosCategoriaForm;


    this.service.criar(dadosCategoria)
      .subscribe({
        next: (response) => {
          this.toast.success('Categoria cadastrado/atualizado com sucesso!');
          this.form.reset();
          this.idCartaoEdicao = null;
      },
      error: (error) => this.onApiError(error)
    });
  }

  private aplicarErrorsValidacao(error: ValidationErrorResponse){
    error.camposInvalidos.forEach(ci => {
      const contro1 = this.form.get(ci.campo);
      if(contro1){
        contro1.setErrors({ apiError: ci.erro });
        contro1.markAsTouched();
      }
    })
  }

  private onApiError(response: any) : void {
      if(response.status === 422){
        this.aplicarErrorsValidacao(response.error);
        this.toast.error('Erro de validação. Verifique os valores informados. ');
        return;
      }
      this.toast.error('Ocorreu um erro ao processar a requisição.');
      console.error(response.error);
  }
}
