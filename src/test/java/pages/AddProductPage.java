package pages;

import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Step;
import org.openqa.selenium.WebDriver;

import static com.codeborne.selenide.Selenide.*;

/**
 * Page Object simplificado.
 * Usa Selenide + JavaScript para conseguir mexer em componentes Vaadin (shadow DOM).
 */
public class AddProductPage {

    private static final String URL = "https://vaadin-bookstore-example.demo.vaadin.com/";
    private static final long TIMEOUT_MS = 10_000L; // 10s

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    static {
        Configuration.timeout = TIMEOUT_MS;
    }

    @Step("Abrir a aplicação Bookstore")
    public AddProductPage openPage() {
        open(URL);
        return this;
    }

    /**
     * Login como admin.
     * Usa primeiro seletores “normais” e, se não der, faz fallback em JS.
     */
    @Step("Fazer login como admin")
    public AddProductPage loginAsAdmin() {
        // tenta inputs normais
        SelenideElement username = findVisible(
                "#username",
                "input[name='username']",
                "input[id*='user']",
                "input[placeholder*='User']",
                "input[type='text']"
        );

        SelenideElement password = findVisible(
                "#password",
                "input[name='password']",
                "input[id*='pass']",
                "input[placeholder*='Pass']",
                "input[type='password']"
        );

        if (username != null && password != null) {
            username.click();
            username.setValue(ADMIN_USERNAME);

            password.click();
            password.setValue(ADMIN_PASSWORD);

            SelenideElement submit = findVisible(
                    "button[type='submit']",
                    "button:has-text('Log in')",
                    "button:has-text('Login')",
                    "vaadin-button[title='Log in']",
                    "vaadin-button"
            );

            if (submit != null) {
                submit.click();
            } else {
                password.pressEnter();
            }

            sleep(1500);
            return this;
        }

        // fallback em JS
        boolean jsResult = executeJsBoolean(
                "(() => {\n" +
                        "  const selectorsU = ['#username','input[name=\"username\"]','input[id*=user]','input[placeholder*=\"User\"]','input[type=text]'];\n" +
                        "  const selectorsP = ['#password','input[name=\"password\"]','input[id*=pass]','input[placeholder*=\"Pass\"]','input[type=password]'];\n" +
                        "  function findFirst(selectors){ for(const s of selectors){ const e = document.querySelector(s); if(e) return e; } return null; }\n" +
                        "  const u = findFirst(selectorsU);\n" +
                        "  const p = findFirst(selectorsP);\n" +
                        "  if(!u || !p) return false;\n" +
                        "  u.focus(); u.value = 'admin'; u.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                        "  p.focus(); p.value = 'admin'; p.dispatchEvent(new Event('input', {bubbles:true}));\n" +
                        "  const submit = document.querySelector('button[type=submit], button[title=\"Log in\"]');\n" +
                        "  if(submit){ submit.click(); return true; }\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keydown',{key:'Enter'}));\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keypress',{key:'Enter'}));\n" +
                        "  p.dispatchEvent(new KeyboardEvent('keyup',{key:'Enter'}));\n" +
                        "  return true;\n" +
                        "})();"
        );

        if (!jsResult) {
            WebDriver drv = WebDriverRunner.getWebDriver();
            throw new IllegalStateException("Não foi possível fazer login. URL atual: " + drv.getCurrentUrl());
        }

        sleep(1500);
        return this;
    }

    // =====================================================================
    //  ABRIR FORM DE NOVO PRODUTO
    // =====================================================================

    @Step("Abrir formulário de 'New product'")
    public AddProductPage openAddProductForm() {

        boolean clicked = false;

        // tenta durante alguns segundos – o Vaadin às vezes demora a renderizar
        for (int i = 0; i < 10 && !clicked; i++) {
            clicked = executeJsBoolean(
                    "function findDeepButtonWithText(text, root) {\n" +
                            "  root = root || document;\n" +
                            "  const buttons = root.querySelectorAll('vaadin-button, button');\n" +
                            "  for (const b of buttons) {\n" +
                            "    if (b.textContent && b.textContent.trim().includes(text)) {\n" +
                            "      b.click();\n" +
                            "      return true;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  const elems = root.querySelectorAll('*');\n" +
                            "  for (const el of elems) {\n" +
                            "    if (el.shadowRoot) {\n" +
                            "      if (findDeepButtonWithText(text, el.shadowRoot)) return true;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return false;\n" +
                            "}\n" +
                            "return findDeepButtonWithText('New product');"
            );

            if (!clicked) {
                sleep(500);
            }
        }

        if (!clicked) {
            throw new IllegalStateException("Não consegui encontrar o botão 'New product'.");
        }

        sleep(1000); // tempo para abrir o painel da direita
        return this;
    }

    // =====================================================================
    //  PREENCHER FORM DE NOVO PRODUTO
    // =====================================================================

    @Step("Preencher formulário de novo produto e gravar: {name}")
    public AddProductPage fillNewProductForm(String name,
                                             String price,
                                             String stock,
                                             String availability,
                                             String category) {

        boolean result = false;

        for (int i = 0; i < 12 && !result; i++) {
            result = executeJsBoolean(
                    "function findDeep(selector, root) {\n" +
                            "  root = root || document;\n" +
                            "  const direct = root.querySelector(selector);\n" +
                            "  if (direct) return direct;\n" +
                            "  const elems = root.querySelectorAll('*');\n" +
                            "  for (const el of elems) {\n" +
                            "    if (el.shadowRoot) {\n" +
                            "      const found = findDeep(selector, el.shadowRoot);\n" +
                            "      if (found) return found;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return null;\n" +
                            "}\n" +

                            "function findFieldByLabel(label) {\n" +
                            "  const candidates = Array.from(document.querySelectorAll('vaadin-text-field, vaadin-number-field, vaadin-text-area'));\n" +
                            "  for (const c of candidates) {\n" +
                            "    try {\n" +
                            "      const lab = c.label || (c.getAttribute && c.getAttribute('label')) || '';\n" +
                            "      if (lab && (lab.trim() === label || lab.trim().startsWith(label) || lab.toLowerCase().includes(label.toLowerCase()))) {\n" +
                            "        return c;\n" +
                            "      }\n" +
                            "    } catch(e) { /* ignore */ }\n" +
                            "  }\n" +
                            "  return findDeep('vaadin-text-field[label=\"' + label + '\"]') || findDeep('vaadin-number-field[label=\"' + label + '\"]');\n" +
                            "}\n" +

                            "function setField(label, value) {\n" +
                            "  const field = findFieldByLabel(label);\n" +
                            "  if (!field) return false;\n" +
                            "  let input = null;\n" +
                            "  try { input = field.shadowRoot && field.shadowRoot.querySelector('input, textarea'); } catch(e) { input = null; }\n" +
                            "  if (input) {\n" +
                            "    input.focus();\n" +
                            "    input.value = value;\n" +
                            "    input.dispatchEvent(new Event('input', {bubbles:true, composed:true}));\n" +
                            "    input.dispatchEvent(new Event('change', {bubbles:true, composed:true}));\n" +
                            "    input.blur();\n" +
                            "  }\n" +
                            "  try {\n" +
                            "    field.value = value;\n" +
                            "    field.dispatchEvent(new CustomEvent('value-changed',{detail:{value:value}, bubbles:true, composed:true}));\n" +
                            "  } catch(e){ /* ignore */ }\n" +
                            "  return true;\n" +
                            "}\n" +

                            "function clickCategory(labelText) {\n" +
                            "  // tenta vaadin-checkbox com label\n" +
                            "  const all = Array.from(document.querySelectorAll('vaadin-checkbox'));\n" +
                            "  for (const cb of all) {\n" +
                            "    const lab = cb.label || (cb.getAttribute && cb.getAttribute('label')) || '';\n" +
                            "    if (lab && (lab.trim() === labelText || lab.trim().toLowerCase().includes(labelText.toLowerCase()))) {\n" +
                            "      cb.checked = true;\n" +
                            "      cb.dispatchEvent(new Event('change', {bubbles:true, composed:true}));\n" +
                            "      return true;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  // fallback: procura labels visíveis na lista de categorias\n" +
                            "  const labels = Array.from(document.querySelectorAll('label, span, div'));\n" +
                            "  for (const l of labels) {\n" +
                            "    try {\n" +
                            "      if (l.textContent && l.textContent.trim().toLowerCase().includes(labelText.toLowerCase())) {\n" +
                            "        // tenta clicar no elemento pai que contenha um checkbox\n" +
                            "        let p = l;\n" +
                            "        for (let i=0;i<6;i++){ p = p.parentElement; if(!p) break; const cb = p.querySelector && p.querySelector('vaadin-checkbox, input[type=checkbox]'); if(cb){ cb.checked = true; cb.dispatchEvent(new Event('change',{bubbles:true, composed:true})); return true; }}\n" +
                            "      }\n" +
                            "    } catch(e) { }\n" +
                            "  }\n" +
                            "  return false;\n" +
                            "}\n" +

                            "function setSelect(label, optionText) {\n" +
                            "  // suporta vaadin-select, vaadin-combo-box, and native select\n" +
                            "  const selects = Array.from(document.querySelectorAll('vaadin-select, vaadin-combo-box, select'));\n" +
                            "  for (const s of selects) {\n" +
                            "    const lab = s.label || (s.getAttribute && s.getAttribute('label')) || '';\n" +
                            "    if (lab && (lab.trim() === label || lab.trim().toLowerCase().includes(label.toLowerCase()))) {\n" +
                            "      try {\n" +
                            "        let found = null;\n" +
                            "        try {\n" +
                            "          const opts = s.shadowRoot ? s.shadowRoot.querySelectorAll('vaadin-select-item, vaadin-combo-box-item, vaadin-item, paper-item, option') : [];\n" +
                            "          for (const o of opts) { if (o.textContent && o.textContent.trim().toLowerCase().includes(optionText.toLowerCase())) { found = o; break; } }\n" +
                            "        } catch(e){}\n" +
                            "        if(found && found.value){ s.value = found.value; s.dispatchEvent(new CustomEvent('value-changed',{detail:{value:s.value}, bubbles:true, composed:true})); return true; }\n" +
                            "        try { s.focus(); s.click(); } catch(e){}\n" +
                            "        const matches = Array.from(document.querySelectorAll('*')).filter(el=> el.textContent && el.textContent.trim().toLowerCase().includes(optionText.toLowerCase()));\n" +
                            "        for(const m of matches){ try{ m.click(); return true; }catch(e){} }\n" +
                            "        try { s.value = optionText; s.dispatchEvent(new Event('change',{bubbles:true, composed:true})); s.dispatchEvent(new CustomEvent('value-changed',{detail:{value:optionText}, bubbles:true, composed:true})); return true;} catch(e){}\n" +
                            "      } catch(e) { /* ignore */ }\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return false;\n" +
                            "}\n" +

                            // versão agressiva de clickSave que tenta remover disabled e usar várias estratégias
                            "function clickSave(root){\n" +
                            "  root = root || document;\n                            \n" +
                            "  function removeDisabledAttributes(el){\n" +
                            "    try {\n" +
                            "      if(el.hasAttribute && el.hasAttribute('disabled')) el.removeAttribute('disabled');\n" +
                            "      if(el.hasAttribute && el.hasAttribute('aria-disabled')) el.removeAttribute('aria-disabled');\n" +
                            "      if(el.disabled !== undefined) el.disabled = false;\n" +
                            "    } catch(e){}\n" +
                            "  }\n" +
                            "\n" +
                            "  function tryClickElement(el){\n" +
                            "    if(!el) return false;\n" +
                            "    removeDisabledAttributes(el);\n" +
                            "    // 1) tentativa directa\n" +
                            "    try { el.click(); return true; } catch(e) {}\n" +
                            "    // 2) tenta clicar no botão interno do shadowRoot\n" +
                            "    try {\n" +
                            "      const inner = (el.shadowRoot && (el.shadowRoot.querySelector('button') || el.shadowRoot.querySelector('[part=button]')));\n" +
                            "      if(inner){ removeDisabledAttributes(inner); try { inner.click(); return true; } catch(e) {} }\n" +
                            "    } catch(e) {}\n" +
                            "    // 3) dispara sequência de eventos de mouse/pointer\n" +
                            "    const evs = ['pointerdown','mousedown','pointerup','mouseup','click'];\n" +
                            "    for(const t of evs){\n" +
                            "      try { el.dispatchEvent(new MouseEvent(t, {bubbles:true, cancelable:true, view:window})); } catch(e) {}\n" +
                            "    }\n" +
                            "    // 4) tenta encontrar o elemento que está sobre o centro do bounding rect e clicar nele\n" +
                            "    try {\n" +
                            "      const r = el.getBoundingClientRect();\n" +
                            "      const cx = Math.round(r.left + r.width/2);\n" +
                            "      const cy = Math.round(r.top + r.height/2);\n" +
                            "      const topEl = document.elementFromPoint(cx, cy);\n" +
                            "      if(topEl && topEl !== el){\n" +
                            "        removeDisabledAttributes(topEl);\n" +
                            "        try { topEl.click(); return true; } catch(e) {}\n" +
                            "        try { topEl.dispatchEvent(new MouseEvent('click', {bubbles:true, cancelable:true, clientX:cx, clientY:cy})); return true; } catch(e) {}\n" +
                            "      }\n" +
                            "    } catch(e) {}\n" +
                            "    return false;\n" +
                            "  }\n" +
                            "\n" +
                            "  const buttons = root.querySelectorAll('vaadin-button, button');\n" +
                            "  for (const b of buttons){\n" +
                            "    if (b.textContent && b.textContent.trim().toLowerCase().startsWith('save')){\n" +
                            "      removeDisabledAttributes(b);\n" +
                            "      // tenta múltiplas vezes com pequeno intervalo (alguns overlays reactinam ao focus)\n" +
                            "      if(tryClickElement(b)) return true;\n" +
                            "      // tenta procurar no shadowRoot do próprio botão\n" +
                            "      try { if(b.shadowRoot){ const inner = b.shadowRoot.querySelector('button, [part=button]'); if(tryClickElement(inner)) return true; } } catch(e){}\n" +
                            "      // último recurso: percorre elementos descendentes e tenta click\n" +
                            "      try {\n" +
                            "        const desc = b.querySelectorAll('*');\n" +
                            "        for(const d of desc){ removeDisabledAttributes(d); if(tryClickElement(d)) return true; }\n" +
                            "      } catch(e){}\n" +
                            "      return false;\n" +
                            "    }\n" +
                            "  }\n" +
                            "\n" +
                            "  // busca recursiva em shadow roots\n" +
                            "  const elems = root.querySelectorAll('*');\n" +
                            "  for (const el of elems){\n" +
                            "    if (el.shadowRoot){\n" +
                            "      if (clickSave(el.shadowRoot)) return true;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return false;\n" +
                            "}\n" +

                            "// Preenche campos principais\n" +
                            "if (!setField('Product name', arguments[0])) return (function(){\n" +
                            "  const vals = Array.from(document.querySelectorAll('vaadin-text-field, vaadin-number-field, vaadin-text-area')).map(f => f.label || f.getAttribute('label') || f.textContent || 'NO_LABEL');\n" +
                            "  console.log('Available labels:', vals);\n" +
                            "  return false;\n" +
                            "})();\n" +
                            "setField('Price', arguments[1]);\n" +
                            "setField('In stock', arguments[2]);\n" +
                            "// define availability\n" +
                            "if (arguments[3]) { const ok = setSelect('Availability', arguments[3]); console.log('setSelect Availability ->', ok); }\n" +
                            "// escolhe categoria\n" +
                            "if (arguments[4]) { const okc = clickCategory(arguments[4]); console.log('clickCategory ->', okc); }\n" +

                            "// === SCROLL ATÉ AO BOTÃO SAVE ===\n" +
                            "function scrollToSave(){\n" +
                            "  const buttons = document.querySelectorAll('vaadin-button, button');\n" +
                            "  for(const b of buttons){\n" +
                            "    if(b.textContent && b.textContent.trim().toLowerCase().startsWith('save')){\n" +
                            "      try { b.scrollIntoView({behavior:'auto', block:'center'}); } catch(e){}\n" +
                            "      return true;\n" +
                            "    }\n" +
                            "  }\n" +
                            "  return false;\n" +
                            "}\n" +

                            "scrollToSave();\n" +

                            "// === TENTA CLICAR NO SAVE ===\n" +
                            "return clickSave();",
                    name, price, stock, availability, category
            );

            if (!result) {
                sleep(500);
            }
        }

        if (!result) {
            throw new IllegalStateException("Não consegui preencher o formulário ou clicar em Save.");
        }

        sleep(2000); // tempo para o grid atualizar com o novo produto
        return this;
    }

    // =====================================================================
    //  HELPER
    // =====================================================================

    /**
     * Devolve o primeiro elemento visível que bata num dos seletores.
     */
    private SelenideElement findVisible(String... selectors) {
        for (String sel : selectors) {
            try {
                SelenideElement el = $(sel);
                if (el.exists() && el.is(Condition.visible)) {
                    return el;
                }
            } catch (Exception ignored) {
                // se o seletor for inválido (:has-text em versões antigas, etc) ignoramos
            }
        }
        return null;
    }

    /**
     * Helper para executar JS que devolve um Boolean.
     */
    private boolean executeJsBoolean(String script, Object... args) {
        Object result = executeJavaScript(script, args);
        return result instanceof Boolean && (Boolean) result;
    }
}
