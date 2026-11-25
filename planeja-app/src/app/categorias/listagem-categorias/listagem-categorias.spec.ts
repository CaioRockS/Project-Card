import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListagemCategoria } from './listagem-categorias';

describe('ListagemCategoria', () => {
  let component: ListagemCategoria;
  let fixture: ComponentFixture<ListagemCategoria>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListagemCategoria],
    }).compileComponents();

    fixture = TestBed.createComponent(ListagemCategoria);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
