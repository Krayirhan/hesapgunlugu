ERROR HANDLING STANDARD

Goal
- Make failures explicit, typed, and consistent across core modules.

Rules
1) Use Result for recoverable domain/data operations.
   - Public APIs that can fail return Result<T>.
   - Do not throw for expected failures.

2) Use sealed error types for critical flows.
   - Define a sealed interface/class (e.g., DomainError) for app-critical use cases.
   - Map technical exceptions to domain errors at the boundary.

3) Avoid nullable returns in core logic.
   - Prefer Result<T> or sealed error types over null.
   - If null is unavoidable, document it and keep it at the edges only.

4) Never swallow errors.
   - Log + map to Result.failure or explicit error type.

5) UI layer converts Result/Error into UiState.
   - Use a single mapping utility and keep UI error strings localizable.

Checklist for new code
- [ ] Public function returns Result<T> or sealed error.
- [ ] No nullable return for error signaling.
- [ ] Exceptions mapped at boundary (data/remote).
- [ ] UI uses UiState mapping only.
