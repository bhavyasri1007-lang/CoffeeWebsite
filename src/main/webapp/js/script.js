const API = "api";

function toggleNav() {
    document.getElementById("nav").classList.toggle("show");
}

async function loadMenu() {
    const container = document.getElementById("menu-container");

    try {
        const response = await fetch(`${API}/menu`);
        if (!response.ok) throw new Error("Menu request failed");

        const items = await response.json();

        container.innerHTML = items.map(item => `
            <article class="menu-card">
                <div class="menu-icon">${coffeeEmoji(item.name)}</div>
                <h3>${escapeHtml(item.name)}</h3>
                <p>${escapeHtml(item.description)}</p>
                <p class="price">₹${Number(item.price).toFixed(2)}</p>
                <button class="add-btn"
                    onclick='openOrder(${JSON.stringify(item.name)}, ${Number(item.price)})'>
                    Order This
                </button>
            </article>
        `).join("");
    } catch (error) {
        container.innerHTML = `
            <p>Menu could not be loaded. Check MySQL/Tomcat setup.</p>
        `;
    }
}

function coffeeEmoji(name) {
    const n = name.toLowerCase();
    if (n.includes("cake")) return "🍰";
    if (n.includes("cold")) return "🥤";
    if (n.includes("mocha")) return "🍫";
    return "☕";
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

function openOrder(itemName, price) {
    const quantity = prompt(`How many ${itemName} do you want?`, "1");
    if (quantity === null) return;

    const qty = Number(quantity);
    if (!Number.isInteger(qty) || qty < 1) {
        alert("Please enter a valid quantity.");
        return;
    }

    const total = price * qty;

    const customerName = prompt("Enter your name:");
    if (!customerName) return;

    const phone = prompt("Enter your phone number:");
    if (!phone) return;

    const address = prompt("Enter delivery address:");
    if (!address) return;

    placeOrder(customerName, phone, address, itemName, qty, total);
}

async function placeOrder(customerName, phone, address, itemName, quantity, total) {
    const body = new URLSearchParams({
        customerName,
        phone,
        address,
        itemName,
        quantity,
        total
    });

    try {
        const response = await fetch(`${API}/orders`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body
        });

        const result = await response.json();
        alert(result.message);

    } catch (error) {
        alert("Order failed. Make sure Tomcat and MySQL are running.");
    }
}

document.getElementById("contact-form").addEventListener("submit", async function(e) {
    e.preventDefault();

    const resultBox = document.getElementById("contact-result");
    const formData = new FormData(this);
    const body = new URLSearchParams(formData);

    try {
        const response = await fetch(`${API}/contact`, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body
        });

        const result = await response.json();
        resultBox.textContent = result.message;

        if (result.success) this.reset();
    } catch (error) {
        resultBox.textContent = "Could not send message.";
    }
});

loadMenu();
