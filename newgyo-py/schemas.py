from pydantic import BaseModel

class SummaryRequest(BaseModel):
    id: int
    content: str

class SummaryResponse(BaseModel):
    id: int
    summary: str

class ChatRequest(BaseModel):
    sender: str = "User" 
    content: str
       
