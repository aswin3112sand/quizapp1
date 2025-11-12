const form = document.getElementById('quizForm');
if (form) {
  const timeEl = document.getElementById('timeLeft');
  let remaining = Number(form.dataset.duration || 180);

  const updateClock = () => {
    if (!timeEl) return;
    const minutes = String(Math.floor(remaining / 60)).padStart(2, '0');
    const seconds = String(remaining % 60).padStart(2, '0');
    timeEl.textContent = `${minutes}:${seconds}`;
  };

  updateClock();
  const interval = setInterval(() => {
    remaining -= 1;
    if (remaining <= 0) {
      remaining = 0;
      updateClock();
      clearInterval(interval);
      form.submit();
      return;
    }
    updateClock();
  }, 1000);

  form.addEventListener('submit', (event) => {
    const missing = [];
    const cards = document.querySelectorAll('.question-card[data-question-id]');
    cards.forEach((card, index) => {
      const id = card.getAttribute('data-question-id');
      const selected = card.querySelector(`input[name="answers[${id}]"]:checked`);
      card.classList.remove('has-error');
      if (!selected) {
        missing.push(index + 1);
        card.classList.add('has-error');
      }
    });
    if (missing.length) {
      event.preventDefault();
      alert(`Please answer all questions. Missing: ${missing.join(', ')}`);
    }
  });
}
