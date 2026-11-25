import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListagemCartoes } from './listagem-cartoes';

describe('ListagemCartoes', () => {
  let component: ListagemCartoes;
  let fixture: ComponentFixture<ListagemCartoes>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListagemCartoes],
    }).compileComponents();

    fixture = TestBed.createComponent(ListagemCartoes);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
