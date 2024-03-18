import pytest
from src.AhoCorasick import AhoCorasick

@pytest.fixture
def setup(): 
    return AhoCorasick(["he", "she", "hers", "his"])

def test__init__(setup):
    assert setup.goto == {(0, 'h'): 1, (1, 'e'): 2, (0, 's'): 3, (3, 'h'): 4, (4, 'e'): 5, (2, 'r'): 6, (6, 's'): 7, (1, 'i'): 8, (8, 's'): 9, (0, 'e'): 0, (0, 'r'): 0, (0, 'i'): 0}
    assert setup.fail == {1: 0, 3: 0, 2: 0, 8: 0, 4: 1, 6: 0, 9: 3, 5: 2, 7: 3}
    output_types = set()
    for items in setup.output.values():
        for item in items:
            output_types.add(item) 
    assert 'he' in output_types
    assert 'she' in output_types
    assert 'hers' in output_types
    assert 'his' in output_types


def test_search(setup):
    res = setup.search('ahishers')
    assert res['his'] == [1]
    assert res['she'] == [3]
    assert res['he'] == [4]
    assert res['hers'] == [4]
