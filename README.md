# AllGoal - Plataforma de Gamificação Corporativa com IA

O **AllGoal** é uma solução inovadora de engajamento corporativo que utiliza gamificação e Inteligência Artificial para motivar colaboradores. Através de um sistema de metas, recompensas (XP e Moedas) e um mentor virtual inteligente, o sistema transforma a produtividade diária numa jornada de evolução profissional.

---

## 🚀 Acesso à Aplicação em Produção (Deploy)

A aplicação está implantada na nuvem e pode ser acessada publicamente. O login é feito via autenticação OAuth2 do GitHub.

**Link de Acesso:** **[COLOQUE_AQUI_SEU_LINK_DO_RAILWAY]**

*(Exemplo: https://allgoal-production.up.railway.app)*

---

## 🌟 Destaques da Solução

- 🎮 **Motor de Gamificação Completo:** Sistema robusto de cálculo de **Nível**, **XP** (Experiência) e **Moedas**. O progresso é calculado automaticamente via *Stored Functions* no banco de dados Oracle.
- 🤖 **AI Coach (Spring AI):** Um mentor virtual integrado (powered by OpenAI) que analisa o perfil do colaborador, as suas metas pendentes e o seu histórico para dar dicas personalizadas de carreira e produtividade.
- 🏆 **Workflow de Metas:**
    - **Colaborador:** Submete metas realizadas.
    - **Admin:** Painel de gestão para Aprovar ou Rejeitar submissões.
    - **Transação Atômica:** Ao coletar uma recompensa, uma transação única (`@Transactional` + Procedure) atualiza o saldo, o nível e o status da meta simultaneamente, garantindo integridade total.
- 🛒 **Loja de Recompensas & Inventário:** Sistema de troca de moedas por benefícios reais (folgas, vales, mentorias), com controle de estoque e histórico de aquisições.
- 🔄 **Modo Demo (Troca de Perfil):** Funcionalidade exclusiva desenvolvida para apresentações que permite alternar entre a visão de `ADMIN` e `FUNCIONARIO` com um clique no menu do utilizador.
- 🏛️ **Percistencia de dados:**
    - **Oracle:** Dados estruturados, integridade referencial, Triggers de auditoria e Packages PL/SQL para regras de negócio.

---

## 🛠️ Arquitetura e Tecnologias

* **Backend:** Java 17, Spring Boot 3.3.
* **Módulos Spring:** Spring AI, Spring Security (OAuth2), Spring Data JPA, Spring WebMVC.
* **Banco de Dados:**
    * **Oracle:** Persistência principal, Packages (`pkg_gs_workflow`, `pkg_gs_admin`), Procedures e Triggers.
* **Frontend:** Thymeleaf (Server-side rendering), DaisyUI (Componentes), Tailwind CSS (Estilização), Marked.js (Renderização de Markdown no Chat).
* **Build:** Gradle.
* **Deploy:** Docker na plataforma Railway.

---

## 👨‍🏫 Guia de Execução (Ambiente de Desenvolvimento Local)

Este guia destina-se ao professor ou avaliador que precisa rodar o projeto localmente.

### Requisitos de Software

1.  **JDK 17:** [Adoptium Temurin 17](https://adoptium.net/)
2.  **Git:** [git-scm.com](https://git-scm.com/downloads)
3.  **Oracle SQL Developer (Opcional):** Para visualizar o banco Oracle da FIAP.
4.  **IDE:** IntelliJ IDEA (Recomendado) ou VS Code.
5.  **Chave de API (IA):** Uma chave válida da OpenAI, ou utilizar fornecida na documentação de links.

### Passo a Passo para Execução Local

**1. Clone o Repositório:**
```bash
git clone [https://github.com/felipe-2833/AllGoal.git](https://github.com/felipe-2833/AllGoal.git)
```
**2.Setar variaveis de ambiente :**

GitHub: ID e secret -> mandado junto aos links

**3. Link :**
```bash
http://localhost:8080/login
```
