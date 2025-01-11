from dataclasses import dataclass
from typing import Optional


@dataclass
class PluginEntryPoint:
    entry_point_symbol: str
    binary_path: Optional[str] = None
