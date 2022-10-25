package id.graceannafitrisinaga.meetingschedule

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import id.graceannafitrisinaga.meetingschedule.data.Item
import id.graceannafitrisinaga.meetingschedule.databinding.FragmentAddItemBinding


/**
 * Fragmen untuk menambah atau memperbarui item dalam database schedule.
 */
class AddItemFragment : Fragment() {

    // Gunakan delegasi properti Kotlin 'by activityViewModels()' dari artefak fragmen-ktx
    // untuk berbagi ViewModel di seluruh fragmen.
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database
                .itemDao()
        )
    }
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()

    lateinit var item: Item

    // Binding objek instance yang sesuai dengan layout fragment_add_item.xml
    // roperti ini bukan nol antara callback siklus hidup onCreateView() dan onDestroyView(),
    // ketika hierarki tampilan dilampirkan ke fragmen
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Mengembalikan nilai true jika EditTexts tidak kosong
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemCount.text.toString(),
        )
    }

    /**
     * Mengikat tampilan dengan informasi [item] yang diteruskan.
     */
    private fun bind(item: Item) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemCount.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)
            saveAction.setOnClickListener { updateItem() }
        }
    }

    /**
     * Menyisipkan Item baru ke dalam database dan menavigasi ke atas ke fragmen daftar.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemCount.text.toString(),
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Memperbarui Item yang ada dalam database dan menavigasi ke atas ke fragmen daftar.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                this.navigationArgs.itemId,
                this.binding.itemName.text.toString(),
                this.binding.itemCount.text.toString()
            )
            val action = AddItemFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    /**
     * Dipanggil saat tampilan dibuat.
     * Argumen Navigasi itemId menentukan item edit atau tambahkan item baru.
     * Jika itemId positif, metode ini mengambil informasi dari database dan
     * memungkinkan pengguna untuk memperbaruinya.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.retrieveItem(id).observe(this.viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }
    }

    /**
     * Disebut sebelum fragmen dihancurkan.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
