
/*
 * Program Name :
 * Author:
 * Date
 * School
 * Computer Used
 * IDE USed
 * Purpose
 */
import java.io.Serializable;

public class DoubleLinkedList implements Serializable
{
	private static final long serialVersionUID = 1L;

	/*
	 * Class to implement the object used in the DoubleLinkedList
	 * Each element will have pointers to the next and previos element in the list
	 */
	private class DoubleLinkedListElement implements Serializable
	{
		private static final long serialVersionUID = 1L;

		private DoubleLinkedListElement next; // used to reference the next element in the list
		private DoubleLinkedListElement prev; // used to reference the previous element in the list
		private Object data; // used to store data. Type Object will allow the class to be generic

		private DoubleLinkedListElement (Object data)
		{
			// Set class attributes to default value
			next = null;
			prev = null;
			this.data = data;
		}

		/*
		 * Getters and Setters for class' attributes
		 */
		private void setNext (DoubleLinkedListElement instance)
		{
			next = instance;
		}

		private void setPrev (DoubleLinkedListElement instance)
		{
			prev = instance;
		}

		private DoubleLinkedListElement getNext ()
		{
			return (next);
		}

		private DoubleLinkedListElement getPrev ()
		{
			return (prev);
		}

		private Object getData()
		{
			return(data);
		}
	}

	// DoubleLinkedList attributes
	private int counter = 0; // Number of elements in the list
	/*
	 * I decided to use an instance of DoubleLinkedListElement as the root of my list
	 * This will allow to keep track of the head and the tail of my list.
	 * root pointer needs to be invisible at DoubleLinkedList users, hence declared private
	 */
	private DoubleLinkedListElement root;
	private DoubleLinkedListElement current; // this will point to the current element of the list

	public DoubleLinkedList()
	{
		root = new DoubleLinkedListElement(null);
		/*
		 * Next and Previous pointers of the root element to point at root itself
		 * This will create a circular list. While going through the list if we
		 * reach the root element, we know we are at top/bottom of the list
		 */
		root.setNext(root);
		root.setPrev(root);
		current = root; // the initial current element is root
	}

	/*
	 * Return the number of elements in the list
	 */
	public int size()
	{
		return (counter);
	}

	/*
	 * Checks wether the list is empty
	 */
	public boolean isEmpty()
	{
		if (counter == 0)
			return true;
		return false;
	}

	/*
	 * Returns the data contained by the current element
	 */
	public Object current()
	{
		if (current == root)
			return(null);
		return(current.getData());
	}

	/*
	 * Returns the data contained in the first element of the list
	 */
	public Object first()
	{
		current = root.getNext();
		if (current == root)
			return(null);
		return (current.getData());
	}

	/*
	 * Returns the data contained in the last element of the list
	 */
	public Object last()
	{
		current = root.getPrev();
		if (current == root)
			return(null);
		return (current.getData());
	}

	/*
	 * Returns the data contained in the element before the current
	 * If the previous element is root then returns null
	 */
	public Object prev()
	{
		if (current.getPrev() == root)
		{
			current = root;
			return(null);
		}
		current = current.getPrev();
		return (current.getData());
	}

	/*
	 * Returns the data contained in the element after the current
	 * If the next element is root then returns null
	 */
	public Object next()
	{
		if (current.getNext() == root)
		{
			current = root;
			return(null);
		}
		current = current.getNext();
		return (current.getData());
	}

	/*
	 * Inserts a new element in the list after the current one.
	 * A new element is always inserted between 2 existing nodes.
	 * Being a circular list even if root is the only element, we can pretend to have 2
	 * since we always have next and previous pointing to an element
	 */
	public void insertAfter(Object data)
	{
		DoubleLinkedListElement newElement = new DoubleLinkedListElement(data);
		// set next and prev pointers of the new element
		// we are inserting after the current
		newElement.setNext(current.getNext()); // next of the new element should be the same as the next of current
		newElement.setPrev(current); // we inserting after current, hence previous of the new element is current
		current.getNext().setPrev(newElement); // previous of the next to current element is the new element
		current.setNext(newElement); // next of the current element is the new element
		current = newElement; // the new element becomes the current element
		counter ++; // increment the number of items in the list
	}

	/*
	 * Inserts a new element in the list before the current one.
	 * A new element is always inserted between 2 existing nodes.
	 * Being a circular list even if root is the only element, we can pretend to have 2
	 * since we always have next and previous pointing to an element
	 */
	public void insertBefore(Object data)
	{
		DoubleLinkedListElement newElement = new DoubleLinkedListElement(data);
		// Same logic used as in insertAfter
		newElement.setPrev(current.getPrev());
		newElement.setNext(current);
		current.getPrev().setNext(newElement);
		current.setPrev(newElement);
		current = newElement;
		counter ++;
	}

	/*
	 * Inserts a new element as the head of the list
	 */
	public void insertHead(Object data)
	{
		// The head is the next element after root
		// we set current to root and use insertAfter to insert it
		current = root;
		insertAfter(data);
		current = root.getNext(); // Set current to the Head of the list
	}

	/*
	 * Inserts a new element as the tail of the list
	 */
	public void insertTail(Object data)
	{
		// The tail is the last element before root
		// we set current to root and use insertBefore to insert it
		current = root;
		insertBefore(data);
		current = root.getPrev(); // Set current to the Tail of the list
	}

	/*
	 * Removes the current element from the list
	 * Moves the current to point to the previous element in the list
	 * if the current element is the Head it will point to the new Head
	 * Return the data cotainded in the new current element
	 */
	public Object removeCurrent()
	{
		if(current == root) // root can't be removed. returns null
			return(null);
		DoubleLinkedListElement temp = current; // use a temp pointer to remove
		/*
		 * Moves current accordingly with the logic explained above
		 */
		// if(current.getPrev() != root)
		// current = current.getPrev();
		// else
		current = current.getNext();
		// Physically remove it from the list by setting prev and next elements' pointers to each other
		temp.getPrev().setNext(temp.getNext());
		temp.getNext().setPrev(temp.getPrev());
		// Set next and prev to null as they were at default creation
		temp.setNext(null);
		temp.setPrev(null);
		counter--; // decrement the number of elements in the list
		if(current == root)
			return(null);
		else
			return(current.getData());
	}

	/*
	 * Removes the Head element from the list
	 * To do it, sets the current element to the Head and call removeCurrent
	 * removeCurrent will set the current pointer to the new Head (or root if
	 * the list is empty)
	 */
	public Object removeHead()
	{
		first();
		return(removeCurrent());
	}

	/*
	 * Removes the Tail element from the list
	 * To do it, sets the current element to the Tail and call removeCurrent
	 * removeCurrent will set the current pointer to the new Head (or root if
	 * the list is empty)
	 */
	public Object removeTail()
	{
		last();
		return(removeCurrent());
	}

	/*
	 * Removes all elements from the list
	 * To do it, sets current at first and keeps calling removeCurrent until
	 * the list is empty.
	 */
	public void clearList()
	{
		first();
		while(counter > 0)
		{
			removeCurrent();
		}
	}

	/*
	 * Removes all elements from the list
	 * To do it, sets current at first and keeps calling removeCurrent until
	 * the list is empty.
	 */
	public Object get(int i)
	{
		if ((counter == 0) || (i >= counter) || (i < 0))
			return(null);

		DoubleLinkedListElement temp = root.next;
		for(int y = 0; y < i; y++)
		{
			temp = temp.next;
		}
		return temp.getData();
	}

	public DoubleLinkedListElement getPointerToCurrent()
	{
		return current;
	}

	public void setPointerToCurrent(DoubleLinkedListElement current)
	{
		this.current = current;
	}
	private boolean hasNextPage = false, hasPrevPage = false;
	private DoubleLinkedListElement firstItemOnPage = null, lastItemOnPage = null;
	private int itemsPerPage = 0;

	public boolean hasNextPage()
	{
		return hasNextPage;
	}

	public boolean hasPrevPage()
	{
		return hasPrevPage;
	}

	public void initPageManager(int itemsPerPage)
	{
		if (counter == 0)
		{
			lastItemOnPage = firstItemOnPage = null;
			itemsPerPage = 0;
		}

		this.itemsPerPage = itemsPerPage;
		hasNextPage = true;
		lastItemOnPage = firstItemOnPage = root.getNext();	
	}

	public DoubleLinkedList getNextPage()
	{
		if (!hasNextPage)
			return(null);

		DoubleLinkedList retList = new DoubleLinkedList();
		firstItemOnPage = lastItemOnPage;
		int i = 0;
		do
		{
			retList.insertTail(lastItemOnPage.getData());
			lastItemOnPage = lastItemOnPage.getNext();
		} while((++i < itemsPerPage) && (lastItemOnPage != root));
		if (lastItemOnPage == root)
			hasNextPage = false;
		else
			hasNextPage = true;
		if (firstItemOnPage.getPrev() == root)
			hasPrevPage = false;
		else
			hasPrevPage = true;
		return(retList);
	}

	public DoubleLinkedList getPrevPage()
	{
		if (!hasPrevPage)
			return(null);
		DoubleLinkedList retList = new DoubleLinkedList();
		for(int i = 0; i < itemsPerPage && firstItemOnPage.getPrev() != root; i++)
			firstItemOnPage = firstItemOnPage.getPrev();
		lastItemOnPage = firstItemOnPage;
		hasNextPage = true;
		retList = getNextPage();
		return(retList);
	}

}
