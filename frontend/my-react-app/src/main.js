import './index.css';

const TOKENS = {
  ADMIN: 'SAWAI_ADMIN_TOKEN_2026',
  AGENT: 'SAWAI_AGENT_TOKEN_2026',
};

const MODULES = {
  customers: {
    title: 'Customers',
    endpoint: '/api/customers',
  },
  policies: {
    title: 'Policies',
    endpoint: '/api/policies',
  },
  leads: {
    title: 'Leads',
    endpoint: '/api/leads',
  },
};

const state = {
  apiBase: localStorage.getItem('apiBase') || 'http://localhost:8080',
  role: localStorage.getItem('role') || 'ADMIN',
  activeModule: 'customers',
  status: {
    type: 'info',
    message: 'Start the Spring Boot backend, then use the forms below.',
  },
  customers: [],
  policies: [],
  leads: [],
  editing: {
    customers: null,
    policies: null,
    leads: null,
  },
  filters: {
    customers: { search: '' },
    policies: { search: '', customerId: '' },
    leads: { search: '', status: '' },
  },
};

const root = document.querySelector('#root');

root.addEventListener('click', handleClick);
root.addEventListener('submit', handleSubmit);
root.addEventListener('change', handleChange);

render();
loadAll();

function render() {
  root.innerHTML = `
    <main class="app">
      <header class="topbar">
        <div class="topbar-inner">
          <div class="brand">
            <h1>Sawai Insurance Management</h1>
            <p>Customer, policy, and lead operations secured by X-Auth-Token.</p>
          </div>
          <div class="connection">
            <div class="field">
              <label for="apiBase">Backend URL</label>
              <input class="api-input" id="apiBase" value="${escapeAttr(state.apiBase)}" />
            </div>
            <div class="field">
              <label for="role">Role token</label>
              <select class="role-select" id="role">
                ${option('ADMIN', 'ADMIN', state.role)}
                ${option('AGENT', 'AGENT', state.role)}
              </select>
            </div>
            <button class="btn primary" data-action="refresh" type="button">Refresh</button>
          </div>
        </div>
      </header>

      <section class="shell">
        ${tabsTemplate()}
        ${statusTemplate()}
        ${summaryTemplate()}
        ${moduleTemplate(state.activeModule)}
      </section>
    </main>
  `;
}

function tabsTemplate() {
  return `
    <nav class="tabs" aria-label="Module navigation">
      ${Object.keys(MODULES)
        .map((key) => `
          <button
            class="tab ${state.activeModule === key ? 'active' : ''}"
            data-tab="${key}"
            type="button"
          >
            ${MODULES[key].title}
          </button>
        `)
        .join('')}
    </nav>
  `;
}

function statusTemplate() {
  return `
    <div class="status ${state.status.type === 'error' ? 'error' : ''}">
      ${escapeHtml(state.status.message)}
    </div>
  `;
}

function summaryTemplate() {
  return `
    <div class="summary">
      ${summaryCard('Customers', state.customers.length)}
      ${summaryCard('Policies', state.policies.length)}
      ${summaryCard('Leads', state.leads.length)}
    </div>
  `;
}

function summaryCard(label, count) {
  return `
    <article class="summary-card">
      <strong>${count}</strong>
      <span>${label}</span>
    </article>
  `;
}

function moduleTemplate(moduleName) {
  if (moduleName === 'customers') {
    return customersTemplate();
  }
  if (moduleName === 'policies') {
    return policiesTemplate();
  }
  return leadsTemplate();
}

function customersTemplate() {
  const editingCustomer = findById(state.customers, state.editing.customers);
  const values = editingCustomer || {};

  return `
    <div class="workspace">
      <section class="panel form-panel">
        <h2>${editingCustomer ? 'Edit Customer' : 'Add Customer'}</h2>
        <form class="entity-form" data-form="customers">
          ${input('firstName', 'First name', values.firstName, 'text', true)}
          ${input('lastName', 'Last name', values.lastName, 'text', true)}
          ${input('email', 'Email', values.email, 'email', true)}
          ${input('phoneNumber', 'Phone number', values.phoneNumber, 'text', true)}
          ${input('dateOfBirth', 'Date of birth', values.dateOfBirth, 'date', true)}
          ${select('accountStatus', 'Account status', ['ACTIVE', 'INACTIVE', 'SUSPENDED'], values.accountStatus || 'ACTIVE')}
          ${formActions(editingCustomer)}
        </form>
      </section>

      <section class="panel list-panel">
        <h3>Customer Records</h3>
        ${customerSearchTemplate()}
        ${customersTable()}
      </section>
    </div>
  `;
}

function customerSearchTemplate() {
  return `
    <form class="search-form" data-search="customers">
      ${input('search', 'Search customers', state.filters.customers.search, 'search', false)}
      <div class="actions">
        <button class="btn primary" type="submit">Search</button>
        <button class="btn" data-action="clear-search" data-module="customers" type="button">Clear</button>
      </div>
    </form>
  `;
}

function customersTable() {
  return tableTemplate(
    ['ID', 'Name', 'Email', 'Phone', 'DOB', 'Status', 'Actions'],
    state.customers,
    (customer) => `
      <tr>
        <td>${customer.id}</td>
        <td>${escapeHtml(customer.firstName)} ${escapeHtml(customer.lastName)}</td>
        <td>${escapeHtml(customer.email)}</td>
        <td>${escapeHtml(customer.phoneNumber)}</td>
        <td>${escapeHtml(customer.dateOfBirth)}</td>
        <td>${label(customer.accountStatus)}</td>
        <td>${tableActions('customers', customer.id)}</td>
      </tr>
    `
  );
}

function policiesTemplate() {
  const editingPolicy = findById(state.policies, state.editing.policies);
  const values = editingPolicy || {};

  return `
    <div class="workspace">
      <section class="panel form-panel">
        <h2>${editingPolicy ? 'Edit Policy' : 'Add Policy'}</h2>
        <form class="entity-form" data-form="policies">
          ${input('policyNumber', 'Policy number', values.policyNumber, 'text', true)}
          ${input('policyName', 'Policy name', values.policyName, 'text', true)}
          ${select('policyType', 'Policy type', ['HEALTH', 'LIFE', 'AUTO', 'HOME', 'BUSINESS', 'TRAVEL'], values.policyType || 'HEALTH')}
          ${input('premiumAmount', 'Premium amount', values.premiumAmount, 'number', true, '0.01')}
          ${input('coverageTermMonths', 'Coverage term months', values.coverageTermMonths, 'number', true, '1')}
          ${input('effectiveStartDate', 'Effective start date', values.effectiveStartDate, 'date', true)}
          ${customerSelect('customerId', 'Associated customer', values.customerId, false, 'policyCustomerId')}
          <p class="hint">Create a customer first so policies can store the customer_id foreign key.</p>
          ${formActions(editingPolicy)}
        </form>
      </section>

      <section class="panel list-panel">
        <h3>Policy Records</h3>
        ${policySearchTemplate()}
        ${policiesTable()}
      </section>
    </div>
  `;
}

function policySearchTemplate() {
  return `
    <form class="search-form" data-search="policies">
      <div class="form-row">
        ${input('search', 'Search policies', state.filters.policies.search, 'search', false)}
        ${customerSelect('customerId', 'Filter by customer', state.filters.policies.customerId, true, 'policyCustomerFilter')}
      </div>
      <div class="actions">
        <button class="btn primary" type="submit">Search</button>
        <button class="btn" data-action="clear-search" data-module="policies" type="button">Clear</button>
      </div>
    </form>
  `;
}

function policiesTable() {
  return tableTemplate(
    ['ID', 'Number', 'Policy', 'Type', 'Premium', 'Term', 'Start', 'Customer', 'Actions'],
    state.policies,
    (policy) => `
      <tr>
        <td>${policy.id}</td>
        <td>${escapeHtml(policy.policyNumber)}</td>
        <td>${escapeHtml(policy.policyName)}</td>
        <td>${label(policy.policyType)}</td>
        <td>${money(policy.premiumAmount)}</td>
        <td>${policy.coverageTermMonths} months</td>
        <td>${escapeHtml(policy.effectiveStartDate)}</td>
        <td>${escapeHtml(policy.customerName)}</td>
        <td>${tableActions('policies', policy.id)}</td>
      </tr>
    `
  );
}

function leadsTemplate() {
  const editingLead = findById(state.leads, state.editing.leads);
  const values = editingLead || {};

  return `
    <div class="workspace">
      <section class="panel form-panel">
        <h2>${editingLead ? 'Edit Lead' : 'Add Lead'}</h2>
        <form class="entity-form" data-form="leads">
          ${input('prospectName', 'Prospect name', values.prospectName, 'text', true)}
          ${input('contactInfo', 'Contact info', values.contactInfo, 'text', true)}
          ${input('referralSource', 'Referral source', values.referralSource, 'text', true)}
          ${select('leadStatus', 'Lead status', ['NEW', 'CONTACTED', 'QUALIFIED', 'CONVERTED', 'LOST'], values.leadStatus || 'NEW')}
          ${input('assignedAgentName', 'Assigned agent name', values.assignedAgentName, 'text', true)}
          ${formActions(editingLead)}
        </form>
      </section>

      <section class="panel list-panel">
        <h3>Lead Records</h3>
        ${leadSearchTemplate()}
        ${leadsTable()}
      </section>
    </div>
  `;
}

function leadSearchTemplate() {
  return `
    <form class="search-form" data-search="leads">
      <div class="form-row">
        ${input('search', 'Search leads', state.filters.leads.search, 'search', false)}
        ${select('status', 'Filter by status', ['', 'NEW', 'CONTACTED', 'QUALIFIED', 'CONVERTED', 'LOST'], state.filters.leads.status)}
      </div>
      <div class="actions">
        <button class="btn primary" type="submit">Search</button>
        <button class="btn" data-action="clear-search" data-module="leads" type="button">Clear</button>
      </div>
    </form>
  `;
}

function leadsTable() {
  return tableTemplate(
    ['ID', 'Prospect', 'Contact', 'Source', 'Status', 'Agent', 'Actions'],
    state.leads,
    (lead) => `
      <tr>
        <td>${lead.id}</td>
        <td>${escapeHtml(lead.prospectName)}</td>
        <td>${escapeHtml(lead.contactInfo)}</td>
        <td>${escapeHtml(lead.referralSource)}</td>
        <td>${label(lead.leadStatus)}</td>
        <td>${escapeHtml(lead.assignedAgentName)}</td>
        <td>${tableActions('leads', lead.id)}</td>
      </tr>
    `
  );
}

function tableTemplate(headers, rows, rowTemplate) {
  const body = rows.length
    ? rows.map(rowTemplate).join('')
    : `<tr><td class="empty" colspan="${headers.length}">No records found.</td></tr>`;

  return `
    <div class="table-wrap">
      <table>
        <thead>
          <tr>${headers.map((header) => `<th>${header}</th>`).join('')}</tr>
        </thead>
        <tbody>${body}</tbody>
      </table>
    </div>
  `;
}

function input(name, text, value = '', type = 'text', required = false, step = '') {
  return `
    <div class="field">
      <label for="${name}">${text}</label>
      <input
        id="${name}"
        name="${name}"
        type="${type}"
        value="${escapeAttr(value || '')}"
        ${required ? 'required' : ''}
        ${step ? `step="${step}"` : ''}
      />
    </div>
  `;
}

function select(name, text, values, selectedValue = '') {
  return `
    <div class="field">
      <label for="${name}">${text}</label>
      <select id="${name}" name="${name}">
        ${values.map((value) => option(value, value ? label(value) : 'All', selectedValue)).join('')}
      </select>
    </div>
  `;
}

function customerSelect(name, text, selectedValue = '', includeAll = false, id = name) {
  const options = [
    includeAll ? option('', 'All customers', selectedValue) : '',
    ...state.customers.map((customer) => {
      const fullName = `${customer.firstName} ${customer.lastName}`;
      return option(String(customer.id), `${fullName} - #${customer.id}`, String(selectedValue || ''));
    }),
  ].join('');

  return `
    <div class="field">
      <label for="${id}">${text}</label>
      <select id="${id}" name="${name}" ${includeAll ? '' : 'required'}>
        ${options || '<option value="">No customers available</option>'}
      </select>
    </div>
  `;
}

function option(value, text, selectedValue) {
  return `
    <option value="${escapeAttr(value)}" ${String(value) === String(selectedValue || '') ? 'selected' : ''}>
      ${escapeHtml(text)}
    </option>
  `;
}

function formActions(isEditing) {
  return `
    <div class="actions">
      <button class="btn primary" type="submit">${isEditing ? 'Update' : 'Save'}</button>
      ${isEditing ? '<button class="btn" data-action="cancel-edit" type="button">Cancel</button>' : ''}
    </div>
  `;
}

function tableActions(moduleName, id) {
  return `
    <div class="table-actions">
      <button class="btn" data-action="edit" data-module="${moduleName}" data-id="${id}" type="button">Edit</button>
      <button class="btn danger" data-action="delete" data-module="${moduleName}" data-id="${id}" type="button">Delete</button>
    </div>
  `;
}

async function handleClick(event) {
  const tab = event.target.closest('[data-tab]');
  const actionButton = event.target.closest('[data-action]');

  if (tab) {
    state.activeModule = tab.dataset.tab;
    setStatus('info', `Viewing ${MODULES[state.activeModule].title}.`);
    render();
    await loadModule(state.activeModule);
    return;
  }

  if (!actionButton) {
    return;
  }

  const { action, module: moduleName, id } = actionButton.dataset;

  if (action === 'refresh') {
    await loadAll();
  }

  if (action === 'clear-search') {
    clearFilter(moduleName);
    await loadModule(moduleName);
  }

  if (action === 'edit') {
    state.activeModule = moduleName;
    state.editing[moduleName] = Number(id);
    setStatus('info', `Editing ${MODULES[moduleName].title.slice(0, -1).toLowerCase()} #${id}.`);
    render();
  }

  if (action === 'cancel-edit') {
    state.editing[state.activeModule] = null;
    setStatus('info', 'Edit cancelled.');
    render();
  }

  if (action === 'delete') {
    const ok = window.confirm('Delete this record? The backend allows DELETE only for ADMIN.');
    if (ok) {
      await deleteRecord(moduleName, id);
    }
  }
}

async function handleSubmit(event) {
  event.preventDefault();

  const form = event.target;
  const formModule = form.dataset.form;
  const searchModule = form.dataset.search;

  if (formModule) {
    await saveRecord(formModule, new FormData(form));
  }

  if (searchModule) {
    applyFilter(searchModule, new FormData(form));
    await loadModule(searchModule);
  }
}

function handleChange(event) {
  if (event.target.id === 'apiBase') {
    state.apiBase = event.target.value.trim().replace(/\/$/, '');
    localStorage.setItem('apiBase', state.apiBase);
  }

  if (event.target.id === 'role') {
    state.role = event.target.value;
    localStorage.setItem('role', state.role);
    setStatus('info', `${state.role} token selected. DELETE is accepted only for ADMIN.`);
    render();
  }
}

async function loadAll() {
  try {
    setStatus('info', 'Loading records from the backend...');
    render();
    const [customers, policies, leads] = await Promise.all([
      request('/api/customers'),
      request('/api/policies'),
      request('/api/leads'),
    ]);
    state.customers = customers;
    state.policies = policies;
    state.leads = leads;
    setStatus('info', 'Records loaded successfully.');
  } catch (error) {
    setStatus('error', error.message);
  }
  render();
}

async function loadModule(moduleName) {
  try {
    const endpoint = MODULES[moduleName].endpoint;
    const query = queryFor(moduleName);
    state[moduleName] = await request(`${endpoint}${query}`);
    setStatus('info', `${MODULES[moduleName].title} loaded.`);
  } catch (error) {
    setStatus('error', error.message);
  }
  render();
}

async function saveRecord(moduleName, formData) {
  try {
    const editingId = state.editing[moduleName];
    const method = editingId ? 'PUT' : 'POST';
    const path = `${MODULES[moduleName].endpoint}${editingId ? `/${editingId}` : ''}`;
    await request(path, {
      method,
      body: JSON.stringify(payloadFor(moduleName, formData)),
    });
    state.editing[moduleName] = null;
    setStatus('info', `${MODULES[moduleName].title.slice(0, -1)} saved successfully.`);
    await loadAll();
  } catch (error) {
    setStatus('error', error.message);
    render();
  }
}

async function deleteRecord(moduleName, id) {
  try {
    await request(`${MODULES[moduleName].endpoint}/${id}`, { method: 'DELETE' });
    setStatus('info', `Record #${id} deleted.`);
    await loadAll();
  } catch (error) {
    setStatus('error', error.message);
    render();
  }
}

async function request(path, options = {}) {
  const response = await fetch(`${state.apiBase}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'X-Auth-Token': TOKENS[state.role],
      ...(options.headers || {}),
    },
  });

  if (response.status === 204) {
    return null;
  }

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(data?.message || `${response.status} ${response.statusText}`);
  }

  return data;
}

function payloadFor(moduleName, formData) {
  const value = (name) => String(formData.get(name) || '').trim();

  if (moduleName === 'customers') {
    return {
      firstName: value('firstName'),
      lastName: value('lastName'),
      email: value('email'),
      phoneNumber: value('phoneNumber'),
      dateOfBirth: value('dateOfBirth'),
      accountStatus: value('accountStatus'),
    };
  }

  if (moduleName === 'policies') {
    return {
      policyNumber: value('policyNumber'),
      policyName: value('policyName'),
      policyType: value('policyType'),
      premiumAmount: Number(value('premiumAmount')),
      coverageTermMonths: Number(value('coverageTermMonths')),
      effectiveStartDate: value('effectiveStartDate'),
      customerId: Number(value('customerId')),
    };
  }

  return {
    prospectName: value('prospectName'),
    contactInfo: value('contactInfo'),
    referralSource: value('referralSource'),
    leadStatus: value('leadStatus'),
    assignedAgentName: value('assignedAgentName'),
  };
}

function applyFilter(moduleName, formData) {
  state.filters[moduleName] = Object.fromEntries(formData.entries());
}

function clearFilter(moduleName) {
  if (moduleName === 'customers') {
    state.filters.customers = { search: '' };
  }
  if (moduleName === 'policies') {
    state.filters.policies = { search: '', customerId: '' };
  }
  if (moduleName === 'leads') {
    state.filters.leads = { search: '', status: '' };
  }
}

function queryFor(moduleName) {
  const filters = state.filters[moduleName];
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value) {
      params.set(key, value);
    }
  });

  const query = params.toString();
  return query ? `?${query}` : '';
}

function findById(rows, id) {
  return rows.find((row) => row.id === id);
}

function setStatus(type, message) {
  state.status = { type, message };
}

function money(value) {
  return Number(value || 0).toLocaleString('en-IN', {
    style: 'currency',
    currency: 'INR',
  });
}

function label(value) {
  return String(value || '')
    .toLowerCase()
    .split('_')
    .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
    .join(' ');
}

function escapeHtml(value) {
  return String(value ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function escapeAttr(value) {
  return escapeHtml(value);
}
