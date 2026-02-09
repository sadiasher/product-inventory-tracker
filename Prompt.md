# AI Collaboration Challenge - Bulk Operations Feature

## Feature Request
Add a product update feature that allows users to perform basic update actions (price updates, category changes, deletion) with appropriate confirmation responses and error handling.

## 1. AI Tool Selection

**Which AI tool would you choose and why?**

I will choose Microsoft Copilot with Claude Sonnet 4.5 model. I use this because of fast and high accuracy code reading and suggestions. Its specialized Agentic model, read whole codebase/repository with the alignment of technology stack use in the project, identify problem areas (in case of bug investigation), suggest and code and inject code in appropriate places. additionally, it provides Keep and Undo options to keep and revert its suggested changes.

## 2. Comprehensive Prompt

I am a senior Java developer, have got a task to implement a product update feature for an e-commerce application. The task is to add a update functionality to an existing code while maintaining architectural consistency and error handling standards.

**Objective:**
Implement a update feature that allows users to perform the following actions:
- Update product price
- Change product category
- Delete a product 

Each action must include relevant confirmation responses and comprehensive error handling.

**Codebase Context:**
Review the project: entity, dto, repository and service classes to understand the current data structure, field types, any existing validation patterns. Maintain consistency with the existing codebase architecture—follow the same patterns for dependency injection, exception handling, service layer organization, and response formatting that are already established in the project.

**Requirements:**

1. **Update Operations:**
   - Price updates: Validate that new price is positive
   - Category changes: Ensure the new category exists
   - Deletion: Implement soft delete 

2. **Confirmation & Response:**
   - Provide clear confirmation messages for successful updates
   - Include relevant product details in the response (updated fields, timestamp )
   - Maintain consistent response structure across all three operations, i.e: OK Response
	{
		"status" : 200,
		"message": "updated successfully",
		"timestamp" : ISO 8601: 2026-02-09T17:00:05Z
		"data": {
			"price" : 2000				
		}
	}
	
	Error Responses:
	{
		"status" : 400,	
		"timestamp" : ISO 8601: 2026-02-09T17:00:05Z
		"error": [
			{"field": "price", "message" : "Prica value cannot be negative"}		
		]
	}
	

3. **Error Handling:**
   - Handle invalid input gracefully with specific error messages
   - Manage cases where products don't exist
   - Manage cases where categories don't exist
   - Catch and handle database or service layer exceptions
   - Rollback transaction where price field update is failed
   - Return appropriate HTTP status codes for different failure scenarios

4. **Code Quality:**
   - Follow existing naming conventions and code style in the project
   - Include null checks and input validation where appropriate
   - Ensure the implementation is testable

**Deliverables:**
Provide the complete implementation including the repository layer methods, service layer methods, controller endpoint(s) and class, request/response DTOs if needed, and any exception handling classes required in relevant packages. If package does not exist create one. Include brief comments explaining key logic where it aids clarity.


## 3. Collaboration Approach

**How would you iterate and collaborate with the AI tool to implement this feature?**

Above prompt will generate the basic structure of update feature, with the validations and exception handling, i will review each class and method one by one, and re prompt till each will be generated and worked as expected.

[Your approach here]
