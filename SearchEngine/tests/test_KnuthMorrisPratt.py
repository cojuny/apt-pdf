import pytest
from src.KnuthMorrisPratt import compute_prefix_function, search
from unittest.mock import Mock

@pytest.fixture
def txt():
    return "ABABDABACDABABCABAB"

@pytest.fixture
def pat():
    return "ABABCABAB"

def test_compute_prefix_function(pat):
    assert compute_prefix_function(pat) == [0, 0, 1, 2, 0, 1, 2, 3, 4]

def test_search(txt, pat):
    assert search(txt, pat) == [10]